library(adjclust)

# --- Step 1: Load LD matrix ---
ld_matrix <- as.matrix(read.csv("genAlg/example_matrix.csv", header = FALSE))

# Check if the matrix is symmetric (required for clustering)
if (!all(ld_matrix == t(ld_matrix))) {
  stop("Error: LD matrix is not symmetric!")
}

# --- Step 2: Compute dissimilarity matrix ---
# Using squared LD values (r^2) and converting to dissimilarity: 1 - r^2
diss_matrix <- 1 - ld_matrix^2

# --- Step 3: Perform adjacency-constrained clustering ---
# Type "similarity" because input is similarity-like (r^2)
res <- adjClust(diss_matrix, type = "similarity")

# --- Step 4: Function to retrieve cluster elements recursively ---
get_cluster_elements <- function(merge, index) {
  if (index < 0) {
    # Negative indices correspond to single elements (SNP indices)
    return(-index)
  } else {
    # Positive indices correspond to clusters formed at previous steps
    return(unlist(lapply(merge[index, ], function(x) get_cluster_elements(merge, x))))
  }
}

# --- Step 5: Extract the two clusters before the last merge ---
last_merge <- res$merge[nrow(res$merge), ]  # Last row = last merge step
cluster1 <- get_cluster_elements(res$merge, last_merge[1])
cluster2 <- get_cluster_elements(res$merge, last_merge[2])

# --- Step 6: Print clusters ---
cat("Cluster 1 SNP indices before last merge:\n")
print(cluster1)
cat("Cluster 2 SNP indices before last merge:\n")
print(cluster2)

# --- Step 7: Plot dendrogram and highlight two clusters ---
plot(res, main = "Adjacency-Constrained Clustering Dendrogram")
rect.hclust(res, k = 2, border = c("red", "blue"))  # Draw rectangles around the 2 clusters

# Optional: Save clusters to CSV files
write.csv(cluster1, "cluster1_before_last_merge.csv", row.names = FALSE, quote = FALSE)
write.csv(cluster2, "cluster2_before_last_merge.csv", row.names = FALSE, quote = FALSE)
