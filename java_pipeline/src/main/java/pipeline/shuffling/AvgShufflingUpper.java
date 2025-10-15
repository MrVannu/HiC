package pipeline.shuffling;

import java.io.*;
import java.util.*;
//import pipeline.shuffling.LdEntry;

/**
 * Reorders the LD upper matrix based on the average R² value per SNP.
 * The output is written in the same logical order as the new permutation,
 * so that tools like adjClust can directly preserve the order (no sorting needed in R).
 */
public class AvgShufflingUpper {

    public static void main(String[] args) throws IOException {

        // Default paths
        String defaultInFile = "ld_data/outputs/BASE_ld_upper.tsv";
        String defaultOutFile = "ld_data/outputs/sorted_avg_upper_matrix.tsv";
        String defaultOrderFile = "ld_data/outputs/best_order_upper_avg.tsv";

        // Input/output from args if provided
        String inFile = (args.length > 0) ? args[0] : defaultInFile;
        String outFile = (args.length > 1) ? args[1] : defaultOutFile;
        String orderFile = (args.length > 2) ? args[2] : defaultOrderFile;

        System.out.println("Reading input: " + inFile);

        // Read LD data
        List<LdEntry> ldList = new ArrayList<>();
        Map<Integer, Double> sumMap = new HashMap<>();
        Map<Integer, Integer> countMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inFile))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length < 3) continue;

                int bpA = Integer.parseInt(parts[0]);
                int bpB = Integer.parseInt(parts[1]);
                double r2 = Double.parseDouble(parts[2]);

                ldList.add(new LdEntry(bpA, bpB, r2));

                sumMap.put(bpA, sumMap.getOrDefault(bpA, 0.0) + r2);
                countMap.put(bpA, countMap.getOrDefault(bpA, 0) + 1);

                sumMap.put(bpB, sumMap.getOrDefault(bpB, 0.0) + r2);
                countMap.put(bpB, countMap.getOrDefault(bpB, 0) + 1);
            }
        }

        // Compute average R² per SNP
        Map<Integer, Double> avgMap = new HashMap<>();
        for (int bp : sumMap.keySet()) {
            avgMap.put(bp, sumMap.get(bp) / countMap.get(bp));
        }

        // Sort SNPs by their average R²
        List<Integer> positions = new ArrayList<>(avgMap.keySet());
        positions.sort(Comparator.comparingDouble(avgMap::get)); // low → high avg R²

        // Save this ordering to a file
        try (PrintWriter pw = new PrintWriter(new FileWriter(orderFile))) {
            for (int bp : positions) {
                pw.println(bp);
            }
        }

        System.out.println("Best order (by average R²) saved to: " + orderFile);

        // Build mapping: SNP → new index
        Map<Integer, Integer> posToIndex = new HashMap<>();
        for (int i = 0; i < positions.size(); i++) {
            posToIndex.put(positions.get(i), i);
        }

        // Build lookup: (bpA, bpB) → r²
        Map<String, Double> similarityMap = new HashMap<>();
        for (LdEntry e : ldList) {
            similarityMap.put(e.bpA + "_" + e.bpB, e.r2);
            similarityMap.put(e.bpB + "_" + e.bpA, e.r2);
        }

        // Write reordered matrix in *new permutation order*
        try (PrintWriter pw = new PrintWriter(new FileWriter(outFile))) {
            pw.println("BP_A\tBP_B\tR2");

            for (int i = 0; i < positions.size(); i++) {
                int bpA = positions.get(i);
                for (int j = i + 1; j < positions.size(); j++) {
                    int bpB = positions.get(j);
                    double r2 = similarityMap.getOrDefault(bpA + "_" + bpB, 0.0);
                    pw.printf("%d\t%d\t%.6f%n", bpA, bpB, r2);
                }
            }
        }

        System.out.println("Reordered LD file (by average R²) written to: " + outFile);
    }
}
