package pipeline.shuffling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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

    public static void main(String[] args) throws IOException {

        String inputPath = "ld_data/outputs/BASE_ld_matix.tsv";
        String outputDir = "./ld_data/outputs";
        String outputPath = outputDir + "/sorted_avg_matrix.tsv";

        double[][] inputMatrix = readMatrixFromTSV(inputPath);

        double[][] outputMatrix = sortAvg(inputMatrix);

        Files.createDirectories(Paths.get(outputDir));

        writeMatrixToTSV(outputMatrix, outputPath);

        System.out.println("Output written to: " + outputPath);


    }
}
