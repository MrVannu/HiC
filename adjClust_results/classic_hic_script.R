# Install and load required packages
if (!requireNamespace("stats", quietly = TRUE)) {
  stop("The 'stats' package is required but not available. It should be included in base R.")
}

# Read command-line arguments
args <- commandArgs(trailingOnly = TRUE)
use_base_format <- "-base" %in% args

# Input/output paths
input_file <- "../ld_data/outputs/BASE_ld_matrix.tsv"
output_merge_file <- "./results/classic_hic_clusters_merge.tsv"
#k_clusters <- 3  # Optional: desired number of clusters

# Create results directory if not exists
if (!dir.exists("results")) dir.create("results", recursive = TRUE)

cat("Reading input matrix...\n")

if (use_base_format) {
  cat("Detected -base flag: treating first row and column as headers.\n")
  df <- read.table(input_file, header = TRUE, sep = "\t", check.names = FALSE)
  rownames(df) <- df[[1]]
  df <- df[, -1]
} else {
  cat("No -base flag: reading as raw matrix (no headers).\n")
  df <- read.table(input_file, header = FALSE, sep = "\t")
}

# Convert to numeric matrix
sim <- as.matrix(df)
sim[is.na(sim)] <- 0

# Check symmetry
cat("Max absolute difference between sim and its transpose:", max(abs(sim - t(sim))), "\n")

# Force symmetry
sim <- (sim + t(sim)) / 2
dimnames(sim) <- NULL

if (!isTRUE(all.equal(sim, t(sim)))) {
  stop("Matrix is not symmetric even after symmetrization.")
}

# Convert similarity to distance
cat("Converting similarity matrix to distance matrix...\n")
dist_mat <- as.dist(1 - sim)

# Run classic hierarchical clustering
cat("Running hierarchical clustering using complete linkage...\n")
hc <- hclust(dist_mat, method = "complete")

# Save merge matrix
cat("Saving merge matrix to file...\n")
merge_df <- as.data.frame(hc$merge)
write.table(merge_df, output_merge_file, quote = FALSE, col.names = FALSE, row.names = TRUE, sep = "\t")

cat("Hierarchical clustering completed.\n")
cat("Merge saved to:", output_merge_file, "\n")
