package pipeline;

import java.util.List;
import java.util.Set;

public class Main extends TreeHandler {

     public static void main(String[] args) {
        
        String filePath = "java/res/_clusters.tsv";
        int[][] linkage = Utils.readAdjClustResults(filePath); // Reads the clustering results from a file

        TreeNode root = buildAndPrintTree(linkage); // Builds the tree from the linkage matrix and prints it
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

    
    
}
