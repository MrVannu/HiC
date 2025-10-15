package pipeline.shuffling;

import java.io.*;
import java.util.*;
//import pipeline.shuffling.LdEntry;

/**
 * Reorders the LD upper matrix based on the median R² value per SNP.
 * Outputs a reordered long-format matrix consistent with adjacency clustering.
 */
public class MedShufflingUpper {

    public static void main(String[] args) throws IOException {

        // Default paths
        String defaultInFile = "ld_data/outputs/BASE_ld_upper.tsv";
        String defaultOutFile = "ld_data/outputs/sorted_med_upper_matrix.tsv";
        String defaultOrderFile = "ld_data/outputs/best_order_upper_med.tsv";

        // Input/output from args if provided
        String inFile = (args.length > 0) ? args[0] : defaultInFile;
        String outFile = (args.length > 1) ? args[1] : defaultOutFile;
        String orderFile = (args.length > 2) ? args[2] : defaultOrderFile;

        System.out.println("Reading input: " + inFile);

        // Read LD data
        List<LdEntry> ldList = new ArrayList<>();
        Map<Integer, List<Double>> valueMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inFile))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length < 3) continue;

                int bpA = Integer.parseInt(parts[0]);
                int bpB = Integer.parseInt(parts[1]);
                double r2 = Double.parseDouble(parts[2]);

                ldList.add(new LdEntry(bpA, bpB, r2));

                valueMap.computeIfAbsent(bpA, k -> new ArrayList<>()).add(r2);
                valueMap.computeIfAbsent(bpB, k -> new ArrayList<>()).add(r2);
            }
        }

        // Compute median R² for each position
        Map<Integer, Double> medianMap = new HashMap<>();
        for (Map.Entry<Integer, List<Double>> entry : valueMap.entrySet()) {
            List<Double> vals = entry.getValue();
            Collections.sort(vals);
            int n = vals.size();
            double median = (n % 2 == 0)
                    ? (vals.get(n / 2 - 1) + vals.get(n / 2)) / 2.0
                    : vals.get(n / 2);
            medianMap.put(entry.getKey(), median);
        }

        // Sort positions by median R² (low → high)
        List<Integer> positions = new ArrayList<>(medianMap.keySet());
        positions.sort(Comparator.comparingDouble(medianMap::get));

        // Save permutation order to file
        try (PrintWriter pw = new PrintWriter(new FileWriter(orderFile))) {
            for (int bp : positions) {
                pw.println(bp);
            }
        }

        System.out.println("Best order (by median R²) saved to: " + orderFile);

        // Build lookup for (bpA, bpB) → r²
        Map<String, Double> simMap = new HashMap<>();
        for (LdEntry e : ldList) {
            simMap.put(e.bpA + "_" + e.bpB, e.r2);
            simMap.put(e.bpB + "_" + e.bpA, e.r2);
        }

        // Write reordered upper-triangular matrix following new order
        try (PrintWriter pw = new PrintWriter(new FileWriter(outFile))) {
            pw.println("BP_A\tBP_B\tR2");
            for (int i = 0; i < positions.size(); i++) {
                int bpA = positions.get(i);
                for (int j = i + 1; j < positions.size(); j++) {
                    int bpB = positions.get(j);
                    double r2 = simMap.getOrDefault(bpA + "_" + bpB, 0.0);
                    pw.printf("%d\t%d\t%.6f%n", bpA, bpB, r2);
                }
            }
        }

        System.out.println("Reordered LD file (by median R²) written to: " + outFile);
    }
}
