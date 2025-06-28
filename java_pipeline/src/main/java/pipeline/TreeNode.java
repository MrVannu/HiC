package pipeline;

public class TreeNode {
    String label;
    TreeNode left;
    TreeNode right;
    Integer leafId;

    TreeNode(String label) {
        this.label = label;
    }

    TreeNode(String label, int leafId) {
        this.label = label;
        this.leafId = leafId;
    }

    public boolean isLeaf() {
        return left==null && right==null;
    }
}
