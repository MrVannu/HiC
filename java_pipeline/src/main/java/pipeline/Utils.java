package pipeline;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;



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


    

    public static int getTreeDepth(TreeNode node) {
        if (node == null) return 0;
        if(node.isLeaf()) return 1;
        
        return 1 + Math.max(getTreeDepth(node.left), getTreeDepth(node.right));
    }


        // Reads a TSV file with numeric values into a 2D double array
    public static double[][] readMatrixFromTSV(String filePath) throws IOException {
    List<double[]> rows = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String line;

        // Read and skip header line (column labels)
        if ((line = br.readLine()) == null) {
            throw new IOException("Empty file");
        }

        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue; // skip empty lines
            String[] parts = line.split("\t");
            
            // Skip the first column (row label)
            double[] row = new double[parts.length - 1];

            for (int i = 1; i < parts.length; i++) {
                row[i - 1] = Double.parseDouble(parts[i]);
            }

            rows.add(row);
        }
    }

    // Convert list to 2D array
    double[][] matrix = new double[rows.size()][];
    for (int i = 0; i < rows.size(); i++) {
        matrix[i] = rows.get(i);
    }

    return matrix;
}

    // Writes a 2D double array to TSV file
    public static void writeMatrixToTSV(double[][] matrix, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (double[] row : matrix) {
                for (int i = 0; i < row.length; i++) {
                    bw.write(String.valueOf(row[i]));
                    if (i < row.length - 1) bw.write("\t");
                }
                bw.newLine();
            }
        }
    }
}



