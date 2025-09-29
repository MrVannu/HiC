# Load required package
if (!requireNamespace("adjclust", quietly = TRUE)) {
  install.packages("adjclust", repos = "http://cran.us.r-project.org")
}
library(adjclust)

# Input/output paths
input_file <- "../ld_data/outputs/sorted_avg_upper_matrix.tsv"
output_merge_file <- "./results/sorted_avg_upper_matrix_clusters_merge.tsv"

# Create results directory if NOT exists
if (!dir.exists("results")) dir.create("results", recursive = TRUE)

cat("Reading long-format upper matrix...\n")
df_long <- read.table(input_file, header = TRUE, sep = "\t", stringsAsFactors = FALSE)

# Unique positions and map to indices
nodes <- sort(unique(c(df_long$BP_A, df_long$BP_B)))
n <- length(nodes)
node_to_idx <- setNames(seq_len(n), nodes)

# Initialize empty similarity matrix
sim <- matrix(0, nrow = n, ncol = n)

# Fill the matrix
for (i in seq_len(nrow(df_long))) {
  a <- df_long$BP_A[i]
  b <- df_long$BP_B[i]
  r2 <- df_long$R2[i]
  idx_a <- node_to_idx[as.character(a)]
  idx_b <- node_to_idx[as.character(b)]
  sim[idx_a, idx_b] <- r2
  sim[idx_b, idx_a] <- r2  # to ensure symmetry
}

sim <- (sim + t(sim)) / 2
dimnames(sim) <- NULL

# Check symmetry
if (!isTRUE(all.equal(sim, t(sim)))) {
  stop("Matrix is not symmetric even after symmetrization.")
}

cat("Matrix reconstructed. Running adjClust...\n")

# Perform adjacency-constrained clustering
fit <- adjClust(sim, type = "similarity")

# Save
merge_df <- as.data.frame(fit$merge)
write.table(merge_df, output_merge_file, quote = FALSE, col.names = FALSE, row.names = TRUE, sep = "\t")

cat("Clustering completed. Merge saved to:", output_merge_file, "\n")
