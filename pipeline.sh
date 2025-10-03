#!/bin/bash
set -e

# Check for correct number of arguments
if [ $# -lt 3 ]; then
    echo "Usage: $0 input.vcf partition_size number_partitions"
    echo "Example: $0 raw_data/gemma_data/dataset_1/example_1.vcf 1000 10"
    exit 1
fi

VCF_FILE=$1
PARTITION_SIZE=$2
NUM_PARTITIONS=$3

echo "🚀 Pipeline started..."


# -------------------------
# STEP 1: Partition VCF
# -------------------------
echo "STEP 1: Partitioning of $VCF_FILE"
python3 raw_data/partition_vcf_Optimized.py "$VCF_FILE" "$PARTITION_SIZE" "$NUM_PARTITIONS"
echo "-----> ✅ STEP 1 COMPLETED!"


# -------------------------
# STEP 2: Computing LD values
# -------------------------
echo "STEP 2: Computing ld values"
# Use ld_generator.sh here (future work)
echo "-----> ✅ STEP 2 COMPLETED!"


# -------------------------
# STEP 3: Building sparse matrices for all partitions
# -------------------------
echo "STEP 3: Building sparse matrices for all partitions"
for ((i=1;i<=NUM_PARTITIONS;i++)); do
    LD_FILE="./ld_data/datasets/partition_${i}.ld"
    OUTPUT_PREFIX="./ld_data/outputs/BASE_ld_upper_${i}"

    if [ -f "$LD_FILE" ]; then
        echo "Processing $LD_FILE → $OUTPUT_PREFIX"
        python3 ld_data/matrix_builder_upper.py "$LD_FILE" "$OUTPUT_PREFIX"
    else
        echo "⚠️  Warning: $LD_FILE not found. Skipping."
    fi
done

echo "-----> ✅ STEP 3 COMPLETED!"


# -------------------------
# STEP 4: Averaging & shuffling upper matrices
# -------------------------
echo "STEP 4: Averaging & shuffling upper matrices"

SRC_ROOT="./java_pipeline/src/main/java"
BIN_DIR="$SRC_ROOT/pipeline/bin"  # relative to project folder
mkdir -p "$BIN_DIR"


# Compile all Java files under pipeline recursively
JAVA_FILES=$(find "$SRC_ROOT" -name "*.java")
if [ -z "$JAVA_FILES" ]; then
    echo "Error: No Java files found under $SRC_ROOT"
    exit 1
fi

echo "Compiling Java files..."
javac -d "$BIN_DIR" $JAVA_FILES

# Run the AvgShufflingUpper for each partition
for ((i=1; i<=NUM_PARTITIONS; i++)); do
    INPUT_FILE="./ld_data/outputs/BASE_ld_upper_${i}.tsv"
    OUTPUT_FILE="./ld_data/outputs/sorted_avg_upper_matrix_${i}.tsv"

    if [ -f "$INPUT_FILE" ]; then
        echo "Processing partition $i..."
        java -cp "$BIN_DIR" pipeline.shuffling.AvgShufflingUpper "$INPUT_FILE" "$OUTPUT_FILE"
    else
        echo "⚠️  Warning: $INPUT_FILE does not exist, skipping..."
    fi
done

echo "-----> ✅ STEP 4 COMPLETED!"

# -------------------------
# STEP 5: Median averaging & shuffling upper matrices
# -------------------------
echo "STEP 5: Median-based averaging & shuffling upper matrices"

# Run the MedShufflingUpper for each partition
for ((i=1; i<=NUM_PARTITIONS; i++)); do
    INPUT_FILE="./ld_data/outputs/BASE_ld_upper_${i}.tsv"
    OUTPUT_FILE="./ld_data/outputs/sorted_med_upper_matrix_${i}.tsv"

    if [ -f "$INPUT_FILE" ]; then
        echo "Processing partition $i..."
        java -cp "$BIN_DIR" pipeline.shuffling.MedShufflingUpper "$INPUT_FILE" "$OUTPUT_FILE"
    else
        echo "⚠️  Warning: $INPUT_FILE does not exist, skipping..."
    fi
done

echo "-----> ✅ STEP 5 COMPLETED!"


# -------------------------
# STEP 6: Genetic Algorithm averaging & shuffling upper matrices
# -------------------------
echo "STEP 6: Genetic Algorithm averaging & shuffling upper matrices"

# Run the MedShufflingUpper for each partition
for ((i=1; i<=NUM_PARTITIONS; i++)); do
    INPUT_FILE="./ld_data/outputs/BASE_ld_upper_${i}.tsv"
    OUTPUT_FILE="./ld_data/outputs/best_order_upper_genAlg_${i}.tsv"
    OUTPUT_FILE_LONG="./ld_data/outputs/sorted_genAlg_upper_matrix_${i}.tsv"

    if [ -f "$INPUT_FILE" ]; then
        echo "Processing partition $i..."
        python3 java_pipeline/src/main/java/pipeline/shuffling/GeneticShufflingUpper.py \
            "$INPUT_FILE" "$OUTPUT_FILE" "$OUTPUT_FILE_LONG"
    else
        echo "⚠️  Warning: $INPUT_FILE does not exist, skipping..."
    fi
done

echo "-----> ✅ STEP 6 COMPLETED!"


# -------------------------
# STEP 7: Hierarchical clustering of upper matrices
# -------------------------
echo "STEP 7: Hierarchical clustering of upper matrices"

# R script path
R_SCRIPT="./adjClust_results/classic_hic_script.R"

# Output directory
OUTPUT_DIR="./adjClust_results/results"
mkdir -p "$OUTPUT_DIR"

for ((i=1; i<=NUM_PARTITIONS; i++)); do
    INPUT_FILE="./ld_data/outputs/BASE_ld_upper_${i}.tsv"
    OUTPUT_FILE="$OUTPUT_DIR/classic_clustering_upper_merge_${i}.tsv"

    if [ -f "$INPUT_FILE" ]; then
        echo "Processing partition $i..."
        Rscript "$R_SCRIPT" "$INPUT_FILE" "$OUTPUT_FILE"
    else
        echo "⚠️  Warning: $INPUT_FILE does not exist, skipping..."
    fi
done

echo "-----> ✅ STEP 7 COMPLETED!"


# -------------------------
# STEP 8: Adjacent clustering of upper matrices
# -------------------------
echo "STEP 8: Adjacent clustering of upper matrices"

# R script path
R_SCRIPT="./adjClust_results/adjclust_script_upper.R"

# Output directory
OUTPUT_DIR="./adjClust_results/results"
mkdir -p "$OUTPUT_DIR"

# BASE
for ((i=1; i<=NUM_PARTITIONS; i++)); do
    INPUT_FILE="./ld_data/outputs/BASE_ld_upper_${i}.tsv"
    OUTPUT_FILE="$OUTPUT_DIR/classic_clustering_upper_merge_${i}.tsv"

    if [ -f "$INPUT_FILE" ]; then
        echo "Processing partition $i for BASE order..."
        Rscript "$R_SCRIPT" "$INPUT_FILE" "$OUTPUT_FILE"
    else
        echo "⚠️  Warning: $INPUT_FILE does not exist, skipping..."
    fi
done

# AVG
for ((i=1; i<=NUM_PARTITIONS; i++)); do
    INPUT_FILE="./ld_data/outputs/sorted_avg_upper_matrix_${i}.tsv"
    OUTPUT_FILE="$OUTPUT_DIR/sorted_avg_upper_matrix_clusters_merge_${i}.tsv"

    if [ -f "$INPUT_FILE" ]; then
        echo "Processing partition $i for AVG order..."
        Rscript "$R_SCRIPT" "$INPUT_FILE" "$OUTPUT_FILE"
    else
        echo "⚠️  Warning: $INPUT_FILE does not exist, skipping..."
    fi
done

# MED
for ((i=1; i<=NUM_PARTITIONS; i++)); do
    INPUT_FILE="./ld_data/outputs/sorted_med_upper_matrix_${i}.tsv"
    OUTPUT_FILE="$OUTPUT_DIR/sorted_med_upper_matrix_clusters_merge_${i}.tsv"

    if [ -f "$INPUT_FILE" ]; then
        echo "Processing partition $i for MED order..."
        Rscript "$R_SCRIPT" "$INPUT_FILE" "$OUTPUT_FILE"
    else
        echo "⚠️  Warning: $INPUT_FILE does not exist, skipping..."
    fi
done

# GenAlg
for ((i=1; i<=NUM_PARTITIONS; i++)); do
    INPUT_FILE="./ld_data/outputs/sorted_genAlg_upper_matrix_${i}.tsv"
    OUTPUT_FILE="$OUTPUT_DIR/sorted_genAlg_upper_matrix_clusters_merge_${i}.tsv"

    if [ -f "$INPUT_FILE" ]; then
        echo "Processing partition $i for MED order..."
        Rscript "$R_SCRIPT" "$INPUT_FILE" "$OUTPUT_FILE"
    else
        echo "⚠️  Warning: $INPUT_FILE does not exist, skipping..."
    fi
done

echo "-----> ✅ STEP 8 COMPLETED!"


# -------------------------
# STEP 9: Compute and score metrics for all clustering results
# -------------------------
echo "STEP 9: Compute and score metrics for all clustering results"

OUTPUT_FILE="./Output_final.txt"
rm -f "$OUTPUT_FILE"

java -cp "$BIN_DIR" pipeline.Main --batch "$NUM_PARTITIONS"

echo "Results written to $OUTPUT_FILE"

echo "-----> ✅ STEP 9 COMPLETED!"


echo ""
echo ""
echo "✅✅✅ SUCCESS: Pipeline finished"







