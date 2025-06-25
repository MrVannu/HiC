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
        Set<String> left_leaves_string = extractSetOfLeaves(root.left); // Extracts the leaves of the left subtree
        Set<String> right_leaves_string = extractSetOfLeaves(root.right);

        Set<Integer> left_leaves = Utils.convertLeafLabelsToInts(left_leaves_string);
        Set<Integer> right_leaves = Utils.convertLeafLabelsToInts(right_leaves_string);


        int[][] matrix = {
            {1, 2, 3, 5, 4},
            {4, 5, 6, 9, 2},
            {4, 5, 6, 9, 2}
        };

        List<Pair<Integer, Integer>> product = cartesianProduct(left_leaves, right_leaves);
        System.out.println("Cartesian product: " + product);

        double avg = averageOverMatrix(product, matrix);
        System.out.println("Average matrix value over product: " + avg);

    }

    



    // TO BE TESTES YET - JUST A PROTOTYPE!! 
    public static double sumAverageOverTree(TreeNode node, int[][] matrix) {
        if (node == null || node.left == null || node.right == null) return 0.0;

        Set<String> leftLeavesStr = extractSetOfLeaves(node.left);
        Set<String> rightLeavesStr = extractSetOfLeaves(node.right);

        Set<Integer> leftLeaves = Utils.convertLeafLabelsToInts(leftLeavesStr);
        Set<Integer> rightLeaves = Utils.convertLeafLabelsToInts(rightLeavesStr);

        List<Pair<Integer, Integer>> product = cartesianProduct(leftLeaves, rightLeaves);
        double avg = averageOverMatrix(product, matrix);

        double leftSum = sumAverageOverTree(node.left, matrix);
        double rightSum = sumAverageOverTree(node.right, matrix);

        return avg + leftSum + rightSum;
    }

    
}



