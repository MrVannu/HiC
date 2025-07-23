package pipeline.shuffling;

import java.io.*;
import pipeline.Utils;

/**
 * Handles matrix reshuffling using a precomputed genetic algorithm-based order.
 * 
 * This class assumes that the best column/row order has been computed externally 
 * and saved to a tsv file. The matrix is reordered accordingly.
 */
public class GeneticShuffling {
    
    public static double[] computeColumnGenOrder(double[][] matrix) {
        // MISSING YET - Refer to Python implementation
        return new double[] {0.0, 0.0};
    }


    /**
     * Reads a matrix and reshuffles it using a genetic algorithm order provided in a tsv file.
     *
     * @param inputPath  Path to the input matrix (tsv format)
     * @param outputPath Path to save the reordered matrix
     * @throws IOException If reading or writing fails
     */
    public static void sortGenAlg(String inputPath, String outputPath) throws IOException {
        double[][] ldMatrix = Utils.readLDMatrix(inputPath);
        int[] order = Utils.getPlainIndices("ld_data/outputs/best_order_genAlg.tsv"); // Py generated order
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
        String outputPath = outputDir + "/sorted_genAlg_matrix.tsv";

        GeneticShuffling.sortGenAlg(inputPath, outputPath);
    }

}
