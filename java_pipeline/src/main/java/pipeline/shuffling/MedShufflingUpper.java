package pipeline.shuffling;

import java.io.*;
import java.util.*;
//import pipeline.shuffling.LdEntry;

public class MedShufflingUpper {

    public static void main(String[] args) throws IOException {

        // Default paths
        String defaultInFile = "ld_data/outputs/BASE_ld_upper.tsv";
        String defaultOutFile = "ld_data/outputs/sorted_med_upper_matrix.tsv";
        // Get input/output from args if provided
        String inputPath = (args.length > 0) ? args[0] : defaultInFile;
        String outputPath = (args.length > 1) ? args[1] : defaultOutFile;

        
        // Read long-format LD file
        List<LdEntry> ldList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\s+");
                if (parts.length < 3) continue;

                int bpA = Integer.parseInt(parts[0]);
                int bpB = Integer.parseInt(parts[1]);
                double r2 = Double.parseDouble(parts[2]);
                ldList.add(new LdEntry(bpA, bpB, r2));
            }
        }

        // Compute median per position
        Map<Integer, List<Double>> posToValues = new HashMap<>();
        for (LdEntry e : ldList) {
            posToValues.computeIfAbsent(e.bpA, k -> new ArrayList<>()).add(e.r2);
            posToValues.computeIfAbsent(e.bpB, k -> new ArrayList<>()).add(e.r2);
        }

        Map<Integer, Double> posToMedian = new HashMap<>();
        for (Map.Entry<Integer, List<Double>> entry : posToValues.entrySet()) {
            List<Double> vals = entry.getValue();
            Collections.sort(vals);
            int n = vals.size();
            double median = (n % 2 == 0) ? (vals.get(n/2 -1) + vals.get(n/2)) / 2.0 : vals.get(n/2);
            posToMedian.put(entry.getKey(), median);
        }

        // Sort positions by median and assign new indices
        List<Integer> positions = new ArrayList<>(posToMedian.keySet());
        positions.sort(Comparator.comparingDouble(posToMedian::get));
        Map<Integer, Integer> newOrder = new HashMap<>();
        for (int i = 0; i < positions.size(); i++) {
            newOrder.put(positions.get(i), i);
        }

        // Write reordered LD file
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputPath))) {
            pw.println("BP_A\tBP_B\tR2");
            for (LdEntry e : ldList) {
                int newA = newOrder.get(e.bpA);
                int newB = newOrder.get(e.bpB);
                pw.printf("%d\t%d\t%.6f%n", newA, newB, e.r2);
            }
        }

        System.out.println("Reordered LD file (by medians) written to: " + outputPath);
    }
}
