package pipeline;

import java.util.*;

/**
 * Handles the construction and traversal of hierarchical clustering trees
 * based on a linkage matrix.
 */
public class TreeHandler extends CartesianMatrixEvaluator {
   
    /**
     * Builds a tree structure from the given linkage matrix and prints it.
     * The linkage matrix is expected to be in the format:
     * [cluster_id, left_child_id, right_child_id, distance]
     *
     * @param linkage A 2D integer array representing the clustering steps.
     * @return The root node of the constructed tree.
     */
    public static TreeNode buildAndPrintTree(int[][] linkage) {
        if (linkage == null || linkage.length == 0) {
            System.out.println("Linkage dataset was empty.");
            return null;
        }

        Map<Integer, TreeNode> nodeMap = new HashMap<>();

        // Track the next available unique leafId
        int maxId = 0;

        // Scan for max point ID (negative indices mean leaf points)
        for (int[] row : linkage) {
            int leftId = row[1];
            int rightId = row[2];
            maxId = Math.max(maxId, Math.max(-leftId, -rightId));
        }

        int nextLeafId = maxId + 1; // Start assigning new IDs after the last leaf

        // Constructs the tree
        for (int[] row : linkage) {
            int clusterId = row[0];
            int leftId = row[1];
            int rightId = row[2];

            TreeNode left = (leftId < 0)
                ? new TreeNode("pt" + (-leftId), -leftId)
                : nodeMap.get(leftId);

            TreeNode right = (rightId < 0)
                ? new TreeNode("pt" + (-rightId), -rightId)
                : nodeMap.get(rightId);

            TreeNode parent = new TreeNode("C" + clusterId, nextLeafId++);
            parent.left = left;
            parent.right = right;

            nodeMap.put(clusterId, parent);
        }

        // Root is last cluster in the linkage matrix
        TreeNode root = nodeMap.get(linkage[linkage.length - 1][0]);

        System.out.println("Tree:");
        Utils.printTree(root, "", true);

        return root;
    }


    /**
     * Extracts the set of leaf IDs from the hierarchical clustering tree.
     * 
     * @param root the root node of the tree
     * @return a set of integer leaf IDs contained in the tree, or {@code null} if root is null
     */
    public static Set<Integer> extractSetOfLeaves(TreeNode root) {
        if (root == null) {
            System.out.println("Root is null, cannot extract leaves.");
            return null;
        }

        Set<Integer> leaves = new HashSet<>();
        collectLeaves(root, leaves);

        System.out.println("Leaves: " + leaves);
        return leaves;
    }


    /**
     * Recursively collects leaf node IDs into the given set.
     * 
     * @param node the current tree node
     * @param leaves the set accumulating leaf IDs
     */
    private static void collectLeaves(TreeNode node, Set<Integer> leaves) {
        if (node == null) return;

        // If no children, it is a leaf
        if (node.left == null && node.right == null) leaves.add(node.leafId);
        else {
            collectLeaves(node.left, leaves);
            collectLeaves(node.right, leaves);
        }
    }
   
}
