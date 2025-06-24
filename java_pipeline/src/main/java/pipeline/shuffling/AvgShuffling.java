package pipeline.shuffling;

import java.util.Arrays;
import java.util.Comparator;

public class AvgShuffling {

    public static double[][] sortAvg(double[][] inputMatrix) {
        int rows = inputMatrix.length;
        int cols = inputMatrix[0].length;

        // Compute averages for each column
        Double[] colAverages = new Double[cols];
        for (int j = 0; j < cols; j++) {
            double sum = 0;
            for (int i = 0; i < rows; i++) {
                sum += inputMatrix[i][j];
            }
            colAverages[j] = sum / rows;
        }

        // Store original indices and sort based on averages (descending)
        Integer[] indices = new Integer[cols];
        for (int i = 0; i < cols; i++) indices[i] = i;

        Arrays.sort(indices, Comparator.comparingDouble((Integer i) -> colAverages[i]).reversed());

        // Create new matrix with columns reordered
        double[][] orderedMatrix = new double[rows][cols];
        for (int i = 0; i < cols; i++) {
            int colIndex = indices[i];
            for (int j = 0; j < rows; j++) {
                orderedMatrix[j][i] = inputMatrix[j][colIndex];
            }
        }

        // Print result (similar to Python version)
        System.out.println("Output Matrix:");
        for (double[] row : orderedMatrix) {
            System.out.println(Arrays.toString(row));
        }

        System.out.println("\nOriginal Order: " + Arrays.toString(indices));

        return orderedMatrix;
    }

    public static void main(String[] args) {
        double[][] inputMatrix = {
            {3, 1, 4},
            {6, 50, 9},
            {2, 7, 8}
        };

        sortAvg(inputMatrix);
    }
}
