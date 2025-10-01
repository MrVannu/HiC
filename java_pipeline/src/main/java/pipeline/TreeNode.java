package pipeline;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a node in a binary tree structure.
 * Each node has a label, a leaf ID, and pointers to left and right children.
 * Optimized to cache leaf sets for large trees.
 */
public class TreeNode {
    String label;
    Integer leafId;
    TreeNode left;
    TreeNode right;

    // Cached set of leaf IDs under this node
    private Set<Integer> leafSet;

    /**
     * Constructs a leaf node with a label and an associated leaf ID.
     *
     * @param label  The label of the node.
     * @param leafId The ID of the leaf node.
     */
    public TreeNode(String label, int leafId) {
        this.label = label;
        this.leafId = leafId;
    }

    /**
     * Checks if the node is a leaf (has no children).
     *
     * @return {@code true} if the node is a leaf, {@code false} otherwise.
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }

    /**
     * Returns the set of all leaf IDs under this node.
     * Caches the result to avoid repeated computations on large trees.
     *
     * @return A set of leaf IDs.
     */
    public Set<Integer> getLeafSet() {
        if (leafSet != null) return leafSet; // return cached set

        leafSet = new HashSet<>();
        if (isLeaf()) {
            leafSet.add(leafId);
        } else {
            if (left != null) leafSet.addAll(left.getLeafSet());
            if (right != null) leafSet.addAll(right.getLeafSet());
        }
        return leafSet;
    }
}
