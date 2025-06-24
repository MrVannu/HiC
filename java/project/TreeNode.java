public class TreeNode {
    String label;
    TreeNode left;
    TreeNode right;

    TreeNode(String label) {
        this.label = label;
    }

    public boolean isLeaf() {
        return left==null && right==null;
    }
}
