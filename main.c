#include <stdio.h>
#include "lib/heap.h"
#include "lib/matrix.h"

int main() {
    int n = 4;  // # genes
    int w = 2;  // Window size

    // Creates the LD scores matrix
    double **matrix = createMatrix(n);

    // Placeholder values for LD scores
    matrix[0][0] = 1; matrix[0][1] = 0.2; matrix[0][2] = 0.5; matrix[0][3] = 0.8;
    matrix[1][0] = 0.2; matrix[1][1] = 1; matrix[1][2] = 0.1; matrix[1][3] = 0.4;
    matrix[2][0] = 0.5; matrix[2][1] = 0.1; matrix[2][2] = 1; matrix[2][3] = 0.3;
    matrix[3][0] = 0.8; matrix[3][1] = 0.4; matrix[3][2] = 0.3; matrix[3][3] = 1;

    // Print the LD scores matrix
    printf("Matrice LD Scores:\n");
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            printf("%.2f ", matrix[i][j]);
        }
        printf("\n");
    }

    // Create the heap and load data into it
    MinHeap *heap = createMinHeap(n * w);
    loadHeapFromMatrix(heap, matrix, n, w);

    // Extract and print the first 3 minimum elements
    printf("\nEstrazione dalla Min Heap:\n");
    for (int i = 0; i < 3 && heap->size > 0; i++) {
        GenePair min = extractMin(heap);
        printf("Min estratto: (%d, %d) con score %.2f\n", min.gene1, min.gene2, min.ld_score);
    }

    // Free allocated memory
    freeMatrix(matrix, n);
    freeHeap(heap);

    return 0;
}
