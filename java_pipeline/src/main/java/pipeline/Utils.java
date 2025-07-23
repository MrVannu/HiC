package pipeline;

import java.io.*;
import java.util.*;


/**
 * A collection of utility functions for file I/O, matrix operations, clustering, and tree visualization.
 */
public class Utils {

    /**
     * Recursively prints a tree structure starting from the given node.
     *
     * @param node   The root node of the tree.
     * @param prefix A string prefix used for indentation.
     * @param isTail Whether this node is the last child (used for drawing lines).
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
     * Reads a file containing clustering results in adjacency format.
     * Each line should be tab-separated and represent one clustering merge step. (.tsv file expected)
     * 
     * @param filePath Path to the TSV file.
     * @return A 2D integer array representing the clustering steps.
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

        // Deliberating using int[][] for enabling quick indicization
        return rows.toArray(new int[0][0]);
    }


    /**
     * Calculates the depth of a binary tree.
     *
     * @param node The root node of the tree.
     * @return The maximum depth of the tree.
     */
    public static int getTreeDepth(TreeNode node) {
        if (node == null) return 0;
        if(node.isLeaf()) return 1;
        
        return 1 + Math.max(getTreeDepth(node.left), getTreeDepth(node.right));
    }

    
    /**
     * Reads a linkage disequilibrium (LD) matrix from a tab-separated file.
     * Skips the header row and first column in each row. (number of genetic positions)
     *
     * @param filePath Path to the input TSV file.
     * @return A 2D double array representing the matrix.
     * @throws IOException If an error occurs during file reading.
     */
    public static double[][] readLDMatrix(String filePath) throws IOException {
        List<double[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Read and skip header line (column labels)
            if ((line = br.readLine()) == null) {
                throw new IOException("Empty file");
            }

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
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
    

    /**
     * Writes a 2D double matrix to a TSV file with 6 decimal precision.
     *
     * @param matrix     The matrix to write.
     * @param outputPath Path to the output TSV file.
     * @throws IOException If an error occurs during writing.
     */
    public static void writeMatrixToTSV(double[][] matrix, String outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            for (double[] row : matrix) {
                for (int j = 0; j < row.length; j++) {
                    writer.write(String.format(Locale.US, "%.6f", row[j]));
                    if (j < row.length - 1) writer.write("\t");
                }
                writer.newLine();
            }
        }
    }


    /**
     * Reorders a square matrix based on a given order of row and column indices.
     *
     * @param matrix The original square matrix.
     * @param order  The new order of indices.
     * @return A reordered matrix according to the given order.
     */
    public static double[][] reorderMatrix(double[][] matrix, int[] order) {
        int n = matrix.length;
        double[][] reordered = new double[n][n];

        for (int i = 0; i < n; i++) {
            int row = order[i];
            for (int j = 0; j < n; j++) {
                int col = order[j];
                reordered[i][j] = matrix[row][col];
            }
        }

        return reordered;
    }


    /**
     * Returns the indices that would sort a given array in ascending order.
     *
     * @param values The input array of doubles.
     * @return An array of indices that would sort the input array.
     */
    public static int[] getSortedIndices(double[] values) {
        Integer[] indices = new Integer[values.length];
        for (int i = 0; i < values.length; i++) indices[i] = i;

        Arrays.sort(indices, Comparator.comparingDouble(i -> values[i]));

        return Arrays.stream(indices).mapToInt(i -> i).toArray();
    }


    /**
     * Reads a TSV file containing numeric values and flattens them into a 1D array of integers.
     * Used for loading plain index lists.
     *
     * @param filePath Path to the TSV file.
     * @return An array of integers extracted from the file.
     * @throws IOException If an error occurs while reading the file.
     */
    public static int[] getPlainIndices(String filePath) throws IOException {
        List<Integer> indices = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\t");
                for (String part : parts) {
                    indices.add(Integer.parseInt(part));
                }
            }
        }

        return indices.stream().mapToInt(i -> i).toArray();
    }


}



