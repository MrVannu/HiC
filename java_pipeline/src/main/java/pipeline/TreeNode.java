package pipeline;

/**
 * Represents a node in a binary tree structure.
 * Each node has a label, a leaf ID, and pointers to left and right children
 */
public class TreeNode {
    String label;
    Integer leafId;
    TreeNode left;
    TreeNode right;
    
    /**
     * Constructs a leaf node with a label and an associated leaf ID.
     *
     * @param label  The label of the node.
     * @param leafId The ID of the leaf node.
     */
    TreeNode(String label, int leafId) {
        this.label = label;
        this.leafId = leafId;
    }

    /**
     * Checks if the node is a leaf (has no children).
     *
     * @return {@code true} if the node is a leaf, {@code false} otherwise.
     */
    public boolean isLeaf() {
        return left==null && right==null;
    }
    
}
