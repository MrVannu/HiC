package pipeline.shuffling;

import java.util.Arrays;
import java.util.Comparator;

public class MedShuffling {

    public static double[][] sortMedian(double[][] inputMatrix) {
        int rows = inputMatrix.length;
        int cols = inputMatrix[0].length;

        // Compute median for each column
        Double[] medians = new Double[cols];
        for (int j = 0; j < cols; j++) {
            double[] column = new double[rows];
            for (int i = 0; i < rows; i++) {
                column[i] = inputMatrix[i][j];
            }
            Arrays.sort(column);
            if (rows % 2 == 1) {
                medians[j] = column[rows / 2];
            } else {
                medians[j] = (column[rows / 2 - 1] + column[rows / 2]) / 2.0;
            }
        }

        // Get indices sorted by median descending
        Integer[] indices = new Integer[cols];
        for (int i = 0; i < cols; i++) indices[i] = i;

        Arrays.sort(indices, Comparator.comparingDouble((Integer i) -> medians[i]).reversed());

        // Reorder columns
        double[][] orderedMatrix = new double[rows][cols];
        for (int i = 0; i < cols; i++) {
            int colIndex = indices[i];
            for (int j = 0; j < rows; j++) {
                orderedMatrix[j][i] = inputMatrix[j][colIndex];
            }
        }

        return orderedMatrix;
    }
}
