package pipeline;

import java.util.List;
import java.util.Set;

public class Main extends TreeHandler {

    public static void main(String[] args) {
        
        String filePath = "./adjClust_results/results/sorted_avg_matrix_clusters_merge.tsv";
        int[][] linkage = Utils.readAdjClustResults(filePath); // Reads the clustering results from a file

        TreeNode root = buildAndPrintTree(linkage); // Builds the tree from the linkage matrix and prints it

        int depth = Utils.getTreeDepth(root); // Gets the total depth of the tree (root is at depth 1)
        double metricResult = metricOverTree(root, linkage, depth, 1, 0);
        System.out.println("Average matrix value over product: " + metricResult);
    }

    
    public static double metricOverTree(TreeNode node, int[][] matrix, int depth, int type, int weighted) {
        // 1 = average over matrix
        // 2 = sum over matrix

        // 0 = no weight metric
        // 1 = REWARDING lower weighted average over matrix
        // 2 = PENALIZING lower weighted sum over matrix with depth

        if (node == null || node.left == null || node.right == null){
            //System.out.println("Node is null or does not have both children. Returning 0.0");
            return 0.0; // Return 0.0 for leaf nodes or incomplete nodes (STOP condition for recursion)
        } 

        Set<Integer> leftLeaves = extractSetOfLeaves(node.left);
        Set<Integer> rightLeaves = extractSetOfLeaves(node.right);

        List<Pair<Integer, Integer>> product = cartesianProduct(leftLeaves, rightLeaves);

        double cumulatedValue;
        if (type == 1) { 
            if(weighted == 0) {
                cumulatedValue = averageOverMatrix(product, matrix);
            } else if (weighted == 1) {
                cumulatedValue = averageOverMatrix(product, matrix) * (depth);
            } else if (weighted == 2) {
                cumulatedValue = averageOverMatrix(product, matrix) * (1.0 / depth);
            } else {
                System.out.println("Invalid weighted type specified. Use 0 for no weight, 1 for rewarding, or 2 for penalizing.");
                return -1.0;
            }
        } 
        else if (type == 2) {
            if(weighted == 0) {
                cumulatedValue = sumOverMatrix(product, matrix);
            } else if (weighted == 1) {
                cumulatedValue = sumOverMatrix(product, matrix) * (depth);
            } else if (weighted == 2) {
                cumulatedValue = sumOverMatrix(product, matrix) * (1.0 / depth);
            } else {
                System.out.println("Invalid weighted type specified. Use 0 for no weight,"+ 
                    "1 for rewarding, or 2 for penalizing.");
                return -1.0;
            }
        }
        else {
            System.out.println("Invalid type specified. Use 1 for average or 2 for sum.");
            return -1.0;
        }

        depth = depth - 1; // Decrease depth for child nodes
        double leftCumaltedValue = metricOverTree(node.left, matrix, depth, type, weighted);
        double rightCumaltedValue = metricOverTree(node.right, matrix, depth, type, weighted);

        return cumulatedValue + leftCumaltedValue + rightCumaltedValue;
    }

    
}



