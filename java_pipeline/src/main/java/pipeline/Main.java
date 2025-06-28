package pipeline;

import java.util.List;
import java.util.Set;

public class Main extends TreeHandler {

    public static void main(String[] args) {
        
        String filePath = "java_pipeline/res/_clusters.tsv";
        int[][] linkage = Utils.readAdjClustResults(filePath); // Reads the clustering results from a file

        TreeNode root = buildAndPrintTree(linkage); // Builds the tree from the linkage matrix and prints it
        //System.out.println("Root node: " + root.label);
        //System.out.println("LEFT node: " + root.left.label);
        //System.out.println("RIGHT node: " + root.right.label);


        int[][] matrix = {
            {1, 2, 3, 5, 4},
            {4, 5, 6, 9, 2},
            {4, 5, 6, 9, 2}
        };

        
        double avg = metricOverTree(root, matrix);
        System.out.println("Average matrix value over product: " + avg);

    }

    
    public static double metricOverTree(TreeNode node, int[][] matrix) {
        if (node == null || node.left == null || node.right == null) return 0.0;

        Set<Integer> leftLeaves = extractSetOfLeaves(node.left);
        Set<Integer> rightLeaves = extractSetOfLeaves(node.right);

        List<Pair<Integer, Integer>> product = cartesianProduct(leftLeaves, rightLeaves);
        double avg = averageOverMatrix(product, matrix);

        double leftSum = metricOverTree(node.left, matrix);
        double rightSum = metricOverTree(node.right, matrix);

        return avg + leftSum + rightSum;
    }

    
}



