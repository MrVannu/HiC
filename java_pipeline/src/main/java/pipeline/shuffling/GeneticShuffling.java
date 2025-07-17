package pipeline.shuffling;

import java.io.*;
import pipeline.Utils;

public class GeneticShuffling {
    
    public static double[] computeColumnGenOrder(double[][] matrix) {
        // MISSING YET
        return new double[] {0.0, 0.0};
    }


    public static void sortAvg(String inputPath, String outputPath) throws IOException {
        double[][] ldMatrix = Utils.readLDMatrix(inputPath);
        int[] order = Utils.getPlainIndices("best_order_genAlg.tsv");
        double[][] reshuffled = Utils.reorderMatrix(ldMatrix, order);
        Utils.writeMatrixToTSV(reshuffled, outputPath);
    }

    public static void main(String[] args) throws IOException {
        String inputPath = "ld_data/outputs/BASE_ld_matrix.tsv";
        String outputDir = "./ld_data/outputs";
        String outputPath = outputDir + "/sorted_genAlg_matrix.tsv";

        AvgShuffling.sortAvg(inputPath, outputPath);
    }
}
