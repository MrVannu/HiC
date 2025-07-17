package pipeline.shuffling;

import java.io.IOException;
import java.util.Arrays;
import pipeline.Utils;

public class MedShuffling {

    public static double[] computeColumnMedian(double[][] matrix) {
        int n = matrix.length;
        double[] medians = new double[n];
        double[] values = new double[n];

        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                values[i] = matrix[i][j];
            }
            Arrays.sort(values);
           
            if (n % 2 == 0) medians[j] = (values[n/2 - 1] + values[n/2]) / 2.0; // Even number of elements
            else medians[j] = values[n/2]; // Odd number of elements
        }

        return medians;
    }





    public static void sortMedian(String inputPath, String outputPath) throws IOException {
        double[][] ldMatrix = Utils.readLDMatrix(inputPath);
        double[] colMeans = computeColumnMedian(ldMatrix);
        int[] order = Utils.getSortedIndices(colMeans);
        double[][] reshuffled = Utils.reorderMatrix(ldMatrix, order);
        Utils.writeMatrixToTSV(reshuffled, outputPath);
    }
    


    public static void main(String[] args) throws IOException {

        String inputPath = "ld_data/outputs/BASE_ld_matrix.tsv";
        String outputDir = "./ld_data/outputs";
        String outputPath = outputDir + "/sorted_med_matrix.tsv";

        MedShuffling.sortMedian(inputPath, outputPath);

    }
}
