import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;



public class Utils {


    /**
     * Recursively prints a tree structure starting from the given node.
     */
    public static void printTree(TreeNode node, String prefix, boolean isTail) {
        if (node == null) return;

        System.out.println(prefix + (isTail ? "└── " : "├── ") + node.label);
        List<TreeNode> children = new ArrayList<>();
        if (node.left != null) children.add(node.left);
        if (node.right != null) children.add(node.right);

        for (int i = 0; i < children.size(); i++) {
            printTree(children.get(i), prefix + (isTail ? "    " : "│   "), i == children.size() - 1);
        }
    }


    /**
     * Reasds a file containing clustering results in adjacency format.
     * The file should contain tab-separated values where each line represents a cluster.
     * The first value is the cluster ID, followed by the IDs of the points in that cluster.
     * 
     * @param filePath Path of the file to read.
     * @return Matrix of integers where each row represents a cluster and its members.
     * @throws Exception If there is an error reading the file.
     */
    public static int[][] readAdjClustResults(String filePath) {
        List<int[]> rows = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\t");
                int[] row = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
                rows.add(row);
            }
        } catch (Exception e) {
            System.err.println("Error while reading the file: " + e.getMessage());
            e.printStackTrace();
        }

        return rows.toArray(new int[0][0]);
    }



    

    public static Set<Integer> convertLeafLabelsToInts(Set<String> leaves) {
    Set<Integer> result = new HashSet<>();
    for (String label : leaves) {
        try {
            // Extract only digits (optionally with minus sign)
            String digits = label.replaceAll("[^\\d-]", "");
            result.add(Integer.parseInt(digits));
        } catch (NumberFormatException e) {
            System.err.println("Could not convert label to integer: " + label);
        }
    }
    return result;
}

    
}



