#include <stdio.h>
#include "lib/heap.h"
#include "lib/matrix.h"

void printCluster(GeneCluster *cluster) {
    if (!cluster) {
        printf("[EMPTY Cluster]\n");
        return;
    }
    printf("LD Score: %.2f | Genes: ", cluster->ld_score);
    for (int i = 0; i < cluster->size; i++) {
        printf("%d ", cluster->genes[i]);
    }
    printf(" -- %p ", cluster->prev);
    printf(" -- %p ", cluster->next);
    printf("\n");
}



int main() {
    int n = 4;
    int w = 2;

    // Create the LD scores matrix
    double **matrix = createMatrix(n);

    // Placeholder values (testing purposes)
    matrix[0][0] = 1.0;  matrix[0][1] = 0.4;  matrix[0][2] = 0.2;  matrix[0][3] = 0.3;
    matrix[1][0] = 0.4;  matrix[1][1] = 1.0;  matrix[1][2] = 0.5;  matrix[1][3] = 0.3;
    matrix[2][0] = 0.2;  matrix[2][1] = 0.5;  matrix[2][2] = 1.0;  matrix[2][3] = 0.1;
    matrix[3][0] = 0.3;  matrix[3][1] = 0.3;  matrix[3][2] = 0.1;  matrix[3][3] = 1.0;


    printf("LD Matrix Scores:\n");
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            printf("%.2f | ", matrix[i][j]);
        }
        // printf("\n----------------------------- ^ row %i", i);
        printf("\n");
    }

    // Create the heap
    MinHeap *heap = createMinHeap(n * w);

    // Inserts intial clusters (pair of genes)
    printf("\nGenes clusters inserted into the Min-Heap structure:\n");
    GeneCluster *prevCluster = NULL;  // Track previous cluster
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n && j - i <= w; j++) {
            int *genes = (int*)malloc(2 * sizeof(int));
            genes[0] = i + 1;
            genes[1] = j + 1;

            GeneCluster *cluster = createCluster(genes, 2, matrix[i][j]);

            // Link clusters in a separate linked list
            if (prevCluster) {
                prevCluster->next = cluster;  // Set next pointer
                cluster->prev = prevCluster;  // Set prev pointer
            }
            prevCluster = cluster;  // Move the tracker

            insertMinHeap(heap, cluster);
            printCluster(cluster);
        }
    }


    // Extraction and merge
    printf("\nExtraction and merge:\n");
    GeneCluster *c1 = extractMin(heap);
    // GeneCluster *c2 = extractMin(heap);
    // GeneCluster *c3 = extractMin(heap);
    // GeneCluster *c4 = extractMin(heap);

    printf("Root->");
    printCluster(c1);



    /*
    *
    *   DEV NOTE: Why the root is always the second smallest element instead of the smallest?
    *   TO BE FIXED 
    * 
    */

    // Final state of the Heap
    printf("\nFinal state of the Heap:\n");
    for (int i = 0; i < heap->size; i++) {
        printCluster(heap->data[i]);
    }

    // Free up memory
    freeMatrix(matrix, n);
    freeHeap(heap);

    return 0;
}
