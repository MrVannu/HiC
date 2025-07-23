package pipeline.shuffling;

import java.io.*;
import pipeline.Utils;

/**
 * Provides functionality to compute column average values of a square matrix
 * and reorder the matrix based on sorted values.
 */
public class AvgShuffling {

    /**
     * Computes the average (mean) of each column in a square matrix.
     *
     * @param matrix A square double matrix (n x n)
     * @return An array of column averages
     */
    public static double[] computeColumnAverages(double[][] matrix) {
        int n = matrix.length;
        double[] means = new double[n];

        for (int j = 0; j < n; j++) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += matrix[i][j];
            }
            means[j] = sum / n;
        }

        return means;
    }


    /**
     * Sorts a matrix by computing the column averages and reordering
     * both rows and columns according to the sorted average values.
     *
     * @param inputPath  Path to the input matrix file (TSV format)
     * @param outputPath Path to write the reshuffled matrix
     * @throws IOException If an I/O error occurs
     */
    public static void sortAvg(String inputPath, String outputPath) throws IOException {
        double[][] ldMatrix = Utils.readLDMatrix(inputPath);
        double[] colMeans = computeColumnAverages(ldMatrix);
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
        String outputPath = outputDir + "/sorted_avg_matrix.tsv";

        AvgShuffling.sortAvg(inputPath, outputPath);
    }
    
}
