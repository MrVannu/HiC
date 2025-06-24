package pipeline;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class TreeHandlerTest {

    @Test
    public void testBuildAndPrintTree_returnsCorrectRoot() {
        int[][] linkage = {
            {1, -1, -2},   // Cluster 1: pt1 and pt2
            {2, -3, -4},   // Cluster 2: pt3 and pt4
            {3, 1, 2}      // Cluster 3: Cluster1 and Cluster2
        };

        TreeNode root = TreeHandler.buildAndPrintTree(linkage);
        assertNotNull(root);
        assertEquals("C3", root.label);
        assertEquals("C1", root.left.label);
        assertEquals("C2", root.right.label);
    }

    @Test
    public void testExtractSetOfLeaves_returnsAllLeaves() {
        TreeNode pt1 = new TreeNode("pt1");
        TreeNode pt2 = new TreeNode("pt2");
        TreeNode cluster1 = new TreeNode("C1");
        cluster1.left = pt1;
        cluster1.right = pt2;

        Set<String> leaves = TreeHandler.extractSetOfLeaves(cluster1);
        assertNotNull(leaves);
        assertEquals(2, leaves.size());
        assertTrue(leaves.contains("pt1"));
        assertTrue(leaves.contains("pt2"));
    }

    @Test
    public void testExtractSetOfLeaves_singleLeaf() {
        TreeNode single = new TreeNode("pt42");
        Set<String> leaves = TreeHandler.extractSetOfLeaves(single);
        assertNotNull(leaves);
        assertEquals(1, leaves.size());
        assertTrue(leaves.contains("pt42"));
    }

    @Test
    public void testBuildAndPrintTree_emptyInput() {
        TreeNode root = TreeHandler.buildAndPrintTree(new int[0][0]);
        assertNull(root);
    }

    @Test
    public void testExtractSetOfLeaves_nullRoot() {
        Set<String> leaves = TreeHandler.extractSetOfLeaves(null);
        assertNull(leaves);
    }
}

