package pipeline;

import java.util.*;

/**
 * Handles the construction and traversal of hierarchical clustering trees
 * based on a linkage matrix. Optimized for large datasets.
 */
public class TreeHandler extends CartesianMatrixEvaluator {

    /**
     * Builds a tree structure from the given linkage matrix.
     * Avoids printing large trees to console for huge datasets.
     *
     * @param linkage A 2D integer array representing the clustering steps.
     * @return The root node of the constructed tree.
     */
    public static TreeNode buildTree(int[][] linkage) {
        if (linkage == null || linkage.length == 0) {
            System.out.println("Linkage dataset was empty.");
            return null;
        }

        Map<Integer, TreeNode> nodeMap = new HashMap<>();

        // Track the maximum leaf ID from negative indices
        int maxLeafId = 0;
        for (int[] row : linkage) {
            int leftId = row[1];
            int rightId = row[2];
            maxLeafId = Math.max(maxLeafId, Math.max(-leftId, -rightId));
        }

        int nextLeafId = maxLeafId + 1;

        // Construct tree
        for (int[] row : linkage) {
            int clusterId = row[0];
            int leftId = row[1];
            int rightId = row[2];

            TreeNode left = (leftId < 0) ? new TreeNode("pt" + (-leftId), -leftId) : nodeMap.get(leftId);
            TreeNode right = (rightId < 0) ? new TreeNode("pt" + (-rightId), -rightId) : nodeMap.get(rightId);

            TreeNode parent = new TreeNode("C" + clusterId, nextLeafId++);
            parent.left = left;
            parent.right = right;

            nodeMap.put(clusterId, parent);
        }

        return nodeMap.get(linkage[linkage.length - 1][0]);
    }

    /**
     * Efficiently extracts the set of leaf IDs from the hierarchical tree.
     * Uses caching inside TreeNode to avoid repeated traversal.
     *
     * @param root the root node of the tree
     * @return a set of integer leaf IDs contained in the tree
     */
    public static Set<Integer> extractSetOfLeaves(TreeNode root) {
        if (root == null) return Collections.emptySet();

        // Use the cached getLeafSet() from TreeNode
        return root.getLeafSet();
    }

    /**
     * Optional: prints a small summary of the tree for debugging.
     * Avoids printing huge trees to console.
     */
    public static void printTreeSummary(TreeNode root) {
        if (root == null) {
            System.out.println("Tree is empty.");
            return;
        }

        int totalLeaves = root.getLeafSet().size();
        System.out.println("Tree summary:");
        System.out.println("Root label: " + root.label);
        System.out.println("Total leaves: " + totalLeaves);
        System.out.println("Left child: " + (root.left != null ? root.left.label : "null"));
        System.out.println("Right child: " + (root.right != null ? root.right.label : "null"));
    }
}
