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

        TreeNode root = TreeHandler.buildTree(linkage);
        assertNotNull(root);
        assertEquals("C3", root.label);
        assertEquals("C1", root.left.label);
        assertEquals("C2", root.right.label);
    }

    @Test
    public void testExtractLeafIds_returnsAllLeafIds() {
        TreeNode pt1 = new TreeNode("pt1", 1);
        TreeNode pt2 = new TreeNode("pt2", 2);
        TreeNode cluster1 = new TreeNode("C1",3);
        cluster1.left = pt1;
        cluster1.right = pt2;

        Set<Integer> leaves = TreeHandler.extractSetOfLeaves(cluster1);

        assertNotNull(leaves);
        assertEquals(2, leaves.size());
        assertTrue(leaves.contains(1));
        assertTrue(leaves.contains(2));
    }


    @Test
    public void testExtractSetOfLeaves_singleLeaf() {
        TreeNode single = new TreeNode("pt42",42);
        Set<Integer> leaves = TreeHandler.extractSetOfLeaves(single);
        assertNotNull(leaves);
        assertEquals(1, leaves.size());
        assertTrue(leaves.contains(42));
    }


    @Test
    public void testExtractSetOfLeaves_nullRoot() {
        Set<Integer> leaves = TreeHandler.extractSetOfLeaves(null);
        assertNull(leaves);
    }
}

