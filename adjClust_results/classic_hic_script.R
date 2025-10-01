# Install Matrix package if not already installed
if (!requireNamespace("Matrix", quietly = TRUE)) install.packages("Matrix")
library(Matrix)

# Input file
input_file <- "../ld_data/outputs/BASE_ld_upper.tsv"

# Read long-format LD file
df <- read.table(input_file, header = TRUE, sep = "\t", stringsAsFactors = FALSE)
df <- df[!is.na(df$BP_A) & !is.na(df$BP_B) & !is.na(df$R2), ]

# Map positions to indices
positions <- sort(unique(c(df$BP_A, df$BP_B)))
pos_to_idx <- setNames(seq_along(positions), positions)

# Build sparse matrix (upper triangular)
i <- pos_to_idx[as.character(df$BP_A)]
j <- pos_to_idx[as.character(df$BP_B)]
x <- df$R2

# Create sparse matrix (symmetric)
sparse_sim <- sparseMatrix(
  i = i, j = j, x = x,
  dims = c(length(positions), length(positions)),
  symmetric = TRUE
)

# Convert similarity to distance
dist_mat <- as.dist(1 - sparse_sim)

# Hierarchical clustering
hc <- hclust(dist_mat, method = "complete")

# Save merge matrix
merge_df <- as.data.frame(hc$merge)
write.table(merge_df, "./results/classic_clustering_upper_merge.tsv", quote = FALSE,
            col.names = FALSE, row.names = TRUE, sep = "\t")

cat("Hierarchical clustering completed. Merge saved.\n")
