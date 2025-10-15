package pipeline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Entry point for the pipeline. Reads a clustering linkage matrix,
 * builds a binary tree, and evaluates a metric recursively over the tree.
 */
public class Main extends TreeHandler {

    public static void main(String[] args) {
        try {
            // Defaults
            String defaultFile = "./adjClust_results/results/sorted_avg_upper_matrix_clusters_merge.tsv";
            int type = 1;      // average
            int weighted = 0;  // none
    
            if (args.length == 0) {
                // No args → run on default file
                runSingleFile(defaultFile, type, weighted);
            } else if ("--batch".equalsIgnoreCase(args[0])) {
                int numPartitions = (args.length > 1) ? Integer.parseInt(args[1]) : 1;
                runBatchMode(numPartitions);
            } else {
                // Args → custom run
                String filePath = args[0];
                if (args.length > 1) type = Integer.parseInt(args[1]);
                if (args.length > 2) weighted = Integer.parseInt(args[2]);
    
                runSingleFile(filePath, type, weighted);
            }
        } catch (Exception e) {
            System.err.println("Pipeline execution failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

      /**
     * Runs metric computation on a single file.
     */
    private static void runSingleFile(String filePath, int type, int weighted) throws Exception {
        int[][] linkage = Utils.readAdjClustResults(filePath);
        TreeNode root = buildTree(linkage);
        int depth = Utils.getTreeDepth(root);
        double metricResult = metricOverTree(root, linkage, depth, type, weighted);

        System.out.printf("File: %s | type=%d | weighted=%d | Metric=%.5f%n",
                filePath, type, weighted, metricResult);
    }

    /**
     * Runs metric computation on all clustering result files for all partitions,
     * and writes results to ./Output_final.txt
     */
    private static void runBatchMode(int numPartitions) throws Exception {
        String[] patterns = {
                "./adjClust_results/results/adjclust_clustering_upper_merge_%d.tsv",
                "./adjClust_results/results/classic_clustering_upper_merge_%d.tsv",
                "./adjClust_results/results/sorted_avg_upper_matrix_clusters_merge_%d.tsv",
                "./adjClust_results/results/sorted_med_upper_matrix_clusters_merge_%d.tsv",
                "./adjClust_results/results/sorted_genAlg_upper_matrix_clusters_merge_%d.tsv"
        };

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./Output_final.txt"))) {
            writer.write("===== METRIC RESULTS =====\n");

            for (int i = 1; i <= numPartitions; i++) {
                writer.write("\n--- Partition " + i + " ---\n");
                System.out.println("\n--- Partition " + i + " ---");

                for (String pattern : patterns) {
                    String filePath = String.format(pattern, i);
                    File f = new File(filePath);

                    if (!f.exists()) {
                        String warning = "⚠️ Skipping missing file: " + filePath;
                        System.out.println(warning);
                        writer.write(warning + "\n");
                        continue;
                    }
                
                    if (f.length() == 0) {
                        String warning = "⚠️ Skipping empty file: " + filePath;
                        System.out.println(warning);
                        writer.write(warning + "\n");
                        continue;
                    }

                    try {
                        int[][] linkage = Utils.readAdjClustResults(filePath);

                        TreeNode root = buildTree(linkage);
                        int depth = Utils.getTreeDepth(root);

                        for (int type = 1; type <= 2; type++) {
                            for (int weighted = 0; weighted <= 2; weighted++) {
                                double metricResult = metricOverTree(root, linkage, depth, type, weighted);

                                String result = String.format(
                                        "File=%s | type=%d | weighted=%d | Metric=%.5f",
                                        filePath, type, weighted, metricResult
                                );

                                System.out.println(result);
                                writer.write(result + "\n");
                            }
                        }
                    } catch (IOException e) {
                        String warning = "⚠️ Skipping missing file: " + filePath;
                        System.out.println(warning);
                        writer.write(warning + "\n");
                    }
                }
            }
        }
        System.out.println("\n✅ All metrics written to ./Output_final.txt");
    }
    
    /**
     * Recursively computes a metric over the binary tree.
     * Metric is based on the Cartesian product of leaf sets at internal nodes.
     *
     * @param node     Current node in the tree
     * @param matrix   Pairwise matrix (similarity/distance)
     * @param depth    Current depth (root starts at total depth)
     * @param type     Metric type: 1 = average, 2 = sum
     * @param weighted Weight strategy: 0 = none, 1 = reward deeper, 2 = penalize deeper
     * @return cumulative metric value
     */
    public static double metricOverTree(TreeNode node, int[][] matrix, int depth, int type, int weighted) {
        if (node == null || node.isLeaf()) {
            return 0.0; // Base case: leaf nodes contribute nothing
        }

        Set<Integer> leftLeaves = extractSetOfLeaves(node.left);
        Set<Integer> rightLeaves = extractSetOfLeaves(node.right);

        List<Pair<Integer, Integer>> product = cartesianProduct(leftLeaves, rightLeaves);

        double cumulatedValue = computeMetric(product, matrix, depth, type, weighted);

        // Recurse on children with decreased depth
        int childDepth = depth - 1;
        double leftValue = metricOverTree(node.left, matrix, childDepth, type, weighted);
        double rightValue = metricOverTree(node.right, matrix, childDepth, type, weighted);

        return cumulatedValue + leftValue + rightValue;
    }

    /**
     * Helper to compute the metric for a given node's Cartesian product.
     */
    private static double computeMetric(List<Pair<Integer, Integer>> product, int[][] matrix, int depth, int type, int weighted) {
        double value;

        switch (type) {
            case 1 -> value = averageOverMatrix(product, matrix);
            case 2 -> value = sumOverMatrix(product, matrix);
            default -> throw new IllegalArgumentException("Invalid type specified. Use 1 for average or 2 for sum.");
        }

        return switch (weighted) {
            case 0 -> value;
            case 1 -> value * depth;        // Reward deeper nodes
            case 2 -> value / depth;        // Penalize deeper nodes
            default -> throw new IllegalArgumentException("Invalid weighted type. Use 0, 1, or 2.");
        };
    }
}





