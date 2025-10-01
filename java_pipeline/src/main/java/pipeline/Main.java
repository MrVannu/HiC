package pipeline;

import java.util.List;
import java.util.Set;

/**
 * Entry point for the pipeline. Reads a clustering linkage matrix,
 * builds a binary tree, and evaluates a metric recursively over the tree.
 */
public class Main extends TreeHandler {

    public static void main(String[] args) {
        try {
            String filePath = "./adjClust_results/results/sorted_avg_upper_matrix_clusters_merge.tsv";

            // Reads the clustering results from TSV file
            int[][] linkage = Utils.readAdjClustResults(filePath);

            // Build the tree and print its structure
            TreeNode root = buildTree(linkage);

            // Get tree depth
            int depth = Utils.getTreeDepth(root);

            // Compute metric over the tree
            double metricResult = metricOverTree(root, linkage, depth, 1, 0);
            System.out.println("Average matrix value over product: " + metricResult);

        } catch (Exception e) {
            System.err.println("Pipeline execution failed: " + e.getMessage());
            e.printStackTrace();
        }
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
