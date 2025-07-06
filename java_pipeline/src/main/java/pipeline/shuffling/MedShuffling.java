package pipeline.shuffling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

import pipeline.Utils;

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



    public static void main(String[] args) throws IOException {

        String inputPath = "ld_data/outputs/BASE_ld_matix.tsv";
        String outputDir = "./ld_data/outputs";
        String outputPath = outputDir + "/sorted_med_matrix.tsv";

        double[][] inputMatrix = Utils.readMatrixFromTSV(inputPath);

        double[][] outputMatrix = sortMedian(inputMatrix);

        Files.createDirectories(Paths.get(outputDir));

        Utils.writeMatrixToTSV(outputMatrix, outputPath);

        System.out.println("Output written to: " + outputPath);


    }
}
