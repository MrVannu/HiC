package pipeline.shuffling;

import java.io.*;
import java.util.*;
//import pipeline.shuffling.LdEntry;

public class AvgShufflingUpper {

    public static void main(String[] args) throws IOException {
<<<<<<< HEAD
        // Default paths
        String defaultInFile = "ld_data/outputs/BASE_ld_upper.tsv";
        String defaultOutFile = "ld_data/outputs/sorted_avg_upper_matrix.tsv";

        // Get input/output from args if provided
        String inFile = (args.length > 0) ? args[0] : defaultInFile;
        String outFile = (args.length > 1) ? args[1] : defaultOutFile;
=======
        String inFile = "ld_data/outputs/BASE_ld_upper.tsv";
        String outFile = "ld_data/outputs/sorted_avg_upper_matrix.tsv";
>>>>>>> b338905e4c445a3e505dcc59625208fbfa87b1e2

        // Read file
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

        // Compute averages
        Map<Integer, Double> avgMap = new HashMap<>();
        for (int bp : sumMap.keySet()) {
            avgMap.put(bp, sumMap.get(bp) / countMap.get(bp));
        }

        // Sort positions by average
        List<Integer> positions = new ArrayList<>(avgMap.keySet());
        positions.sort(Comparator.comparingDouble(avgMap::get));

        // Map old BP → new index
        Map<Integer, Integer> posToOrder = new HashMap<>();
        for (int i = 0; i < positions.size(); i++) {
            posToOrder.put(positions.get(i), i);
        }

        // Write reordered file
        try (PrintWriter pw = new PrintWriter(new FileWriter(outFile))) {
            pw.println("BP_A\tBP_B\tR2");
            for (LdEntry e : ldList) {
                int newA = posToOrder.get(e.bpA);
                int newB = posToOrder.get(e.bpB);
                pw.printf("%d\t%d\t%.6f%n", newA, newB, e.r2);
            }
        }

        System.out.println("Reordered LD file (by means) written to: " + outFile);
    }
}
