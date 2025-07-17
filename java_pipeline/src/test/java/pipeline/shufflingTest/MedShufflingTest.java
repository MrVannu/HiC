package pipeline.shufflingTest;

import org.junit.jupiter.api.Test;
import pipeline.shuffling.MedShuffling;
import static org.junit.jupiter.api.Assertions.*;

public class MedShufflingTest {

    /* 
    @Test
    public void testSortMedian() {
        double[][] inputMatrix = {
            {3, 1, 4},
            {6, 5, 9},
            {2, 7, 8}
        };

        // Medians:
        // col 0: [2,3,6] => 3
        // col 1: [1,5,7] => 5
        // col 2: [4,8,9] => 8
        // Sorted by median descending: col 2, col 1, col 0

        double[][] expectedMatrix = {
            {4, 1, 3},
            {9, 5, 6},
            {8, 7, 2}
        };

        double[][] resultMatrix = MedShuffling.sortMedian(inputMatrix);

        assertEquals(expectedMatrix.length, resultMatrix.length, "Row count mismatch");
        assertEquals(expectedMatrix[0].length, resultMatrix[0].length, "Column count mismatch");

        for (int i = 0; i < expectedMatrix.length; i++) {
            for (int j = 0; j < expectedMatrix[0].length; j++) {
                assertEquals(expectedMatrix[i][j], resultMatrix[i][j], 0.0001,
                        String.format("Mismatch at row %d col %d", i, j));
            }
        }
    }
*/}
