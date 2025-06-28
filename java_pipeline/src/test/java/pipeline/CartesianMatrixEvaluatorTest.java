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
            new CartesianMatrixEvaluator.Pair<>(2, 1),  // matrix[1][0] = 3
            new CartesianMatrixEvaluator.Pair<>(2, 2)  // matrix[1][1] = 4
        );

        double avg = CartesianMatrixEvaluator.averageOverMatrix(coords, matrix);
        assertEquals((1 + 2 + 3 + 4) / 4.0, avg);
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

    @Test
    public void testBalancedTreeAverageNoWeight() {
        TreeNode root = new TreeNode("root",1);
        root.left = new TreeNode("left",2);
        root.right = new TreeNode("right",3);

        root.left.left = new TreeNode("LL", 4);
        root.left.right = new TreeNode("LR", 5);
        root.right.left = new TreeNode("RL", 6);
        root.right.right = new TreeNode("RR", 7);

        int[][] matrix = {
            {1, 2, 3, 5, 4, 1, 2, 3, 5, 4},
            {4, 5, 6, 9, 2, 4, 5, 6, 9, 2},
            {1, 2, 3, 5, 4, 1, 2, 3, 5, 4},
            {4, 5, 6, 9, 2, 4, 5, 6, 9, 2},
            {1, 2, 3, 5, 4, 1, 2, 3, 5, 4},
            {4, 5, 6, 9, 2, 4, 5, 6, 9, 2},
            {1, 2, 3, 5, 4, 1, 2, 3, 5, 4}
        };

        int depth = Utils.getTreeDepth(root); // Gets the total depth of the tree (root is at depth 1)
        double result = Main.metricOverTree(root, matrix, depth, 1, 0); // average, no weight
        assertTrue(result > 0.0);
    }

    @Test
    public void testBalancedTreeSumNoWeight() {
        TreeNode root = new TreeNode("root",1);
        root.left = new TreeNode("left",2);
        root.right = new TreeNode("right",3);

        root.left.left = new TreeNode("LL", 4);
        root.left.right = new TreeNode("LR", 5);
        root.right.left = new TreeNode("RL", 6);
        root.right.right = new TreeNode("RR", 7);

        int[][] matrix = {
            {1, 2, 3, 5, 4, 1, 2, 3, 5, 4},
            {4, 5, 6, 9, 2, 4, 5, 6, 9, 2},
            {1, 2, 3, 5, 4, 1, 2, 3, 5, 4},
            {4, 5, 6, 9, 2, 4, 5, 6, 9, 2},
            {1, 2, 3, 5, 4, 1, 2, 3, 5, 4},
            {4, 5, 6, 9, 2, 4, 5, 6, 9, 2},
            {1, 2, 3, 5, 4, 1, 2, 3, 5, 4}
        };

        int depth = Utils.getTreeDepth(root); // Gets the total depth of the tree (root is at depth 1)
        double result = Main.metricOverTree(root, matrix, depth, 2, 0); // sum, no weight
        assertTrue(result > 0.0);
    }

    @Test
    public void testSingleNodeTreeReturnsZero() {
        TreeNode root = new TreeNode("leaf", 0);
        int[][] matrix = new int[1][1];
        
        int depth = Utils.getTreeDepth(root); // Should be 1 for a single node
        double result = Main.metricOverTree(root, matrix, depth, 1, 0);
        assertEquals(0.0, result);
    }

    @Test
    public void testSumRewardingWeight() {
        TreeNode root = new TreeNode("root",3);
        root.left = new TreeNode("L",4);
        root.right = new TreeNode("R",7);
        root.left.left = new TreeNode("LL",1);
        root.left.right = new TreeNode("LR", 2);
        root.right.left = new TreeNode("RL", 3);
        root.right.right = new TreeNode("RR", 4);

        int[][] matrix = {
            {1, 2, 3, 5, 4, 1, 2, 3, 5, 4},
            {4, 5, 6, 9, 2, 4, 5, 6, 9, 2},
            {1, 2, 3, 5, 4, 1, 2, 3, 5, 4},
            {4, 5, 6, 9, 2, 4, 5, 6, 9, 2},
            {1, 2, 3, 5, 4, 1, 2, 3, 5, 4},
            {4, 5, 6, 9, 2, 4, 5, 6, 9, 2},
            {1, 2, 3, 5, 4, 1, 2, 3, 5, 4}
        };


        int depth = Utils.getTreeDepth(root); // Gets the total depth of the tree (root is at depth 1)
        double result = Main.metricOverTree(root, matrix, depth, 2, 1); // sum, rewarding
        assertTrue(result > 0);
    }


}
