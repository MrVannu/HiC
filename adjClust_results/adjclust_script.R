# Install and load packages
if (!requireNamespace("adjclust", quietly = TRUE)) {
  install.packages("adjclust", repos = "http://cran.us.r-project.org")
}
library(adjclust)

# Read command-line arguments
args <- commandArgs(trailingOnly = TRUE)
use_base_format <- "-base" %in% args

# Input/output paths
input_file <- "../ld_data/outputs/sorted_genAlg_matrix.tsv"
output_merge_file <- "./results/sorted_genAlg_matrix_clusters_merge.tsv"
#k_clusters <- 3  # desired number of clusters

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

# Check numeric symmetry
cat("Max absolute difference between sim and its transpose:", max(abs(sim - t(sim))), "\n")

# Force symmetry just in case of numeric drift
sim <- (sim + t(sim)) / 2

# Remove row/column names that might confuse isSymmetric
dimnames(sim) <- NULL

# Check symmetry using all.equal (robust to small floating point issues)
if (!isTRUE(all.equal(sim, t(sim)))) {
  stop("Matrix is not symmetric even after symmetrization.")
}

# Perform clustering
cat("Running adjClust...\n")
fit <- adjClust(sim, type = "similarity")

# Save merge matrix
merge_df <- as.data.frame(fit$merge)
write.table(merge_df, output_merge_file, quote = FALSE, col.names = FALSE, row.names = TRUE, sep = "\t")

cat("Clustering completed.\n")
cat("Merge saved to:", output_merge_file, "\n")
