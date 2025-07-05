# HiC

library(Matrix)

# Load the sparse matrix file (3-column format)
df <- read.table("ld_sparse_matrix.tsv", header = FALSE)
colnames(df) <- c("row", "col", "value")

# Convert to sparse matrix
mat <- sparseMatrix(i = df$row + 1,  # R indices start at 1
                    j = df$col + 1,
                    x = df$value)

# Now mat is a sparse matrix you can pass to adjclust or other methods
