library("adjclust")

sim <- matrix(c(1.0, 0.5, 0.2, 0.1,
                0.5, 1.0, 0.1, 0.2,
                0.2, 0.1, 1.0, 0.6,
                0.1, 0.2 ,0.6 ,1.0), nrow=4)
h <- 3
fit <- adjClust(sim, "similarity", h)
#plot(fit)

cat("Writing the cluster to the file.\n\n")
cluster_filename <-paste("","clusters.tsv", sep="_")
merge_df  <- as.data.frame(fit$merge)
write.table(merge_df, cluster_filename, quote=FALSE, col.names = FALSE, row.names = TRUE, sep = "\t")
 
cat("Done\n")