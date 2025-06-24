import java.util.*;

public class CartesianProductCalculator {
    
    // Class to represent a pair (for coordinates)
    public static class Pair<A, B> {
        public final A first;
        public final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }

    /**
     * Returns the cartesian product of two sets.
     */
    public static <T, U> List<Pair<T, U>> cartesianProduct(Set<T> set1, Set<U> set2) {
        List<Pair<T, U>> product = new ArrayList<>();
        for (T a : set1) {
            for (U b : set2) {
                product.add(new Pair<>(a, b));
            }
        }
        return product;
    }

    /**
     * Computes the average of the matrix values at each coordinate in the Cartesian product.
     */
    public static double averageOverMatrix(List<Pair<Integer, Integer>> coordinates, int[][] matrix) {
        if (coordinates.isEmpty()) return 0.0;

        int sum = 0;
        int count = 0;

        for (Pair<Integer, Integer> pair : coordinates) {
            int x = pair.first;
            int y = pair.second;
            //System.out.println("Candidate pair:" + x + ", " + y);

            // Check bounds
            if (x >= 0 && x <= matrix.length && y >= 0 && y <= matrix[0].length) {
                sum += matrix[x-1][y-1]; // Adjust for 1-based indexing
                System.out.println("Adding value: " + matrix[x-1][y-1]);
                count++;
            }
        }

        //System.out.println("Sum: " + sum + ", Count: " + count + ", and rows: " + matrix.length + " and cols :" + matrix[0].length);
        System.out.println("Sum: " + sum + ", Count: " + count);
        return count > 0 ? (double) sum / count : 0.0;
    }
}