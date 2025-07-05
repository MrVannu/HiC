package pipeline;

import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

   
    @Test
    public void testReadAdjClustResults() throws IOException {
        File tempFile = File.createTempFile("test-clusters", ".txt");
        tempFile.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("1\t2\t3\n");
            writer.write("4\t5\n");
            writer.write("6\n");
        }

        int[][] matrix = Utils.readAdjClustResults(tempFile.getAbsolutePath());

        assertEquals(3, matrix.length);
        assertArrayEquals(new int[]{1, 2, 3}, matrix[0]);
        assertArrayEquals(new int[]{4, 5}, matrix[1]);
        assertArrayEquals(new int[]{6}, matrix[2]);
    }

    @Test
    public void testPrintTree() {
        TreeNode root = new TreeNode("root",1);
        root.left = new TreeNode("left",2);
        root.right = new TreeNode("right",3);
        root.left.left = new TreeNode("left.left",4);

        // Capture printed output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Utils.printTree(root, "", true);

        String output = outContent.toString();
        assertTrue(output.contains("root"));
        assertTrue(output.contains("left"));
        assertTrue(output.contains("right"));
        assertTrue(output.contains("left.left"));

        // Restore standard output
        System.setOut(System.out);
    }



    @Test
    public void testNullTree() {
        assertEquals(0, Utils.getTreeDepth(null));
    }

    @Test
    public void testSingleNodeTree() {
        TreeNode root = new TreeNode(null,1);
        assertEquals(1, Utils.getTreeDepth(root));
    }


    @Test
    public void testUnbalancedTree() {
        TreeNode node = new TreeNode("root", 1);
        node.left = new TreeNode("left", 2);
        node.left.left = new TreeNode("left.left", 3);
        node.right = new TreeNode("right", 4);
        
        assertEquals(3, Utils.getTreeDepth(node));
    }
}
