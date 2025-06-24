package pipeline;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class CartesianMatrixEvaluatorTest {

    @Test
    public void testCartesianProduct() {
        Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2));
        Set<String> set2 = new HashSet<>(Arrays.asList("A", "B"));

        List<CartesianMatrixEvaluator.Pair<Integer, String>> result =
                CartesianMatrixEvaluator.cartesianProduct(set1, set2);

        assertEquals(4, result.size());
        assertTrue(result.contains(new CartesianMatrixEvaluator.Pair<>(1, "A")));
        assertTrue(result.contains(new CartesianMatrixEvaluator.Pair<>(1, "B")));
        assertTrue(result.contains(new CartesianMatrixEvaluator.Pair<>(2, "A")));
        assertTrue(result.contains(new CartesianMatrixEvaluator.Pair<>(2, "B")));
    }

    @Test
    public void testAverageOverMatrix() {
        int[][] matrix = {
            {1, 2},
            {3, 4}
        };

        List<CartesianMatrixEvaluator.Pair<Integer, Integer>> coords = Arrays.asList(
            new CartesianMatrixEvaluator.Pair<>(1, 1), // matrix[0][0] = 1
            new CartesianMatrixEvaluator.Pair<>(1, 2), // matrix[0][1] = 2
            new CartesianMatrixEvaluator.Pair<>(2, 2)  // matrix[1][1] = 4
        );

        double avg = CartesianMatrixEvaluator.averageOverMatrix(coords, matrix);
        assertEquals((1 + 2 + 4) / 3.0, avg);
    }

    @Test
    public void testAverageOverMatrixWithEmptyCoordinates() {
        int[][] matrix = {
            {5, 5},
            {5, 5}
        };

        List<CartesianMatrixEvaluator.Pair<Integer, Integer>> coords = new ArrayList<>();
        double avg = CartesianMatrixEvaluator.averageOverMatrix(coords, matrix);
        assertEquals(0.0, avg);
    }

    @Test
    public void testAverageOverMatrixOutOfBoundsIgnored() {
        int[][] matrix = {
            {10, 20},
            {30, 40}
        };

        List<CartesianMatrixEvaluator.Pair<Integer, Integer>> coords = Arrays.asList(
            new CartesianMatrixEvaluator.Pair<>(1, 1), // valid -> 10
            new CartesianMatrixEvaluator.Pair<>(3, 3)  // out of bounds -> ignored
        );

        double avg = CartesianMatrixEvaluator.averageOverMatrix(coords, matrix);
        assertEquals(10.0, avg);
    }
}
