import java.util.*;


public class TreeHandler extends CartesianProductCalculator {

    /**
     * Creates a tree from a linkage matrix and prints it.
     * 
     **/
    public static TreeNode buildAndPrintTree(int[][] linkage) {
        if (linkage == null || linkage.length == 0) {
            System.out.println("Linkage dataset was empty.");
            return null;
        }

        Map<Integer, TreeNode> nodeMap = new HashMap<>();
        // Construct the tree from the linkage matrix
        for (int[] row : linkage) {
            int clusterId = row[0];
            int leftId = row[1];
            int rightId = row[2];

            TreeNode left = (leftId < 0) ? new TreeNode("pt"+(-leftId)) : nodeMap.get(leftId);
            TreeNode right = (rightId < 0) ? new TreeNode("pt"+(-rightId)) : nodeMap.get(rightId);

            TreeNode parent = new TreeNode("C" + clusterId);
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


    public static Set<String> extractSetOfLeaves(TreeNode root) {
    if (root == null) {
        System.out.println("Root is null, cannot extract leaves.");
        return null;
    }

    Set<String> leaves = new HashSet<>();
    collectLeaves(root, leaves);

    System.out.println("Leaves: " + leaves);
    return leaves;
}

    // Helper method to collect leaves from the tree
    private static void collectLeaves(TreeNode node, Set<String> leaves) {
        if (node == null) return;

        // If no children, it's a leaf
        if (node.left == null && node.right == null) {
            leaves.add(node.label);
        } else {
            collectLeaves(node.left, leaves);
            collectLeaves(node.right, leaves);
        }
    }







   

}
