package pipeline;

import java.util.*;

/**
 * Provides utilities for evaluating Cartesian products over matrices.
 * Includes support for computing sum and average of matrix values over
 * coordinate pairs derived from set products.
 */
public class CartesianMatrixEvaluator {
    
     /**
     * A generic immutable pair class.
     *
     * @param <A> Type of the first element
     * @param <B> Type of the second element
     */
    public static class Pair<A, B> {
        public final A first;
        public final B second;

        /**
         * Constructs a new pair.
         *
         * @param first the first value
         * @param second the second value
         */
        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pair)) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }



    /**
     * Generates the Cartesian product of two sets.
     *
     * @param set1 the first input set
     * @param set2 the second input set
     * @return a list of pairs representing all combinations of elements from {@code set1} and {@code set2}
     * @param <T> the type of elements in the first set
     * @param <U> the type of elements in the second set
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
     * Computes the average of the matrix values located at the given coordinate pairs.
     *
     * @param coordinates list of coordinate pairs
     * @param matrix the matrix to access values from
     * @return the average of values at the specified coordinates, or 0.0 if the coordinate list is empty
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
                //System.out.println("Adding value: " + matrix[x-1][y-1]);
                count++;
            }
        }

        //System.out.println("Sum: " + sum + ", #Candidate pairs: " + count);
        //System.out.println("---------------------------------");
        return count > 0 ? (double) sum / count : 0.0;
    }


    /**
     * Computes the sum of the matrix values located at the given coordinate pairs.
     *
     * @param coordinates list of coordinate pairs
     * @param matrix the matrix to access values from
     * @return the sum of values at the specified coordinates, or 0.0 if the coordinate list is empty
     */
    public static double sumOverMatrix(List<Pair<Integer, Integer>> coordinates, int[][] matrix) {
        if (coordinates.isEmpty()) return 0.0;

        int sum = 0;

        for (Pair<Integer, Integer> pair : coordinates) {
            int x = pair.first;
            int y = pair.second;
            //System.out.println("Candidate pair:" + x + ", " + y);

            // Check bounds
            if (x >= 0 && x <= matrix.length && y >= 0 && y <= matrix[0].length) {
                sum += matrix[x-1][y-1]; // Adjust for 1-based indexing
                //System.out.println("Adding value: " + matrix[x-1][y-1]);
            }
        }

        //System.out.println("Sum: " + sum + ", Leaves count: " + count);
        //System.out.println("---------------------------------");
        return sum;
    }

}