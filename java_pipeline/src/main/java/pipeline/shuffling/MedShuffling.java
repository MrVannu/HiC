package pipeline.shuffling;

import java.io.IOException;
import java.util.Arrays;
import pipeline.Utils;

/**
 * Provides functionality to compute column medians of a square matrix
 * and reorder the matrix based on sorted medians.
 */
public class MedShuffling {

    /**
     * Computes the median of each column in a square matrix.
     *
     * @param matrix A square matrix (n x n)
     * @return An array of column medians
     */
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


    /**
     * Reads a matrix from a TSV file, computes the median of each column,
     * sorts the columns and rows by the median order, and writes the result to a new TSV file.
     *
     * @param inputPath  Path to the input matrix (TSV format)
     * @param outputPath Path to save the sorted matrix (TSV format)
     * @throws IOException If file operations fail
     */
    public static void sortMedian(String inputPath, String outputPath) throws IOException {
        double[][] ldMatrix = Utils.readLDMatrix(inputPath);
        double[] colMeans = computeColumnMedian(ldMatrix);
        int[] order = Utils.getSortedIndices(colMeans);
        double[][] reshuffled = Utils.reorderMatrix(ldMatrix, order);
        Utils.writeMatrixToTSV(reshuffled, outputPath);
    }
    


    /**
     * Main method for standalone execution. Uses predefined file paths.
     *
     * @throws IOException If reading or writing files fails
     */
    public static void main(String[] args) throws IOException {

        String inputPath = "ld_data/outputs/BASE_ld_matrix.tsv";
        String outputDir = "./ld_data/outputs";
        String outputPath = outputDir + "/sorted_med_matrix.tsv";

        MedShuffling.sortMedian(inputPath, outputPath);

    }
    
}
