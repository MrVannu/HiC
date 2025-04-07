#include <stdio.h>
#include <stdlib.h>
#include "lib/heap.h"
#include "lib/matrix.h"

void printCluster(GeneCluster *cluster) {
    if (!cluster) {
        printf("[EMPTY Cluster]\n");
        return;
    }
    printf("Cluster at %p | LD Score: %.2f | Genes: [", (void*)cluster, cluster->ld_score);
    for (int i = 0; i < cluster->size; i++) {
        printf("%d", cluster->genes[i]);
        if (i < cluster->size - 1)
            printf(", ");
    }
    printf("] | Prev: %p | Next: %p\n", (void*)cluster->prev, (void*)cluster->next);
}

int main() {
    int n = 4;
    int w = 1;

    // Create the LD scores matrix
    double **matrix = createMatrix(n);

    matrix[0][0] = 1.0;  matrix[0][1] = 0.4;  matrix[0][2] = 0.2;  matrix[0][3] = 0.3;
    matrix[1][0] = 0.4;  matrix[1][1] = 1.0;  matrix[1][2] = 0.5;  matrix[1][3] = 0.3;
    matrix[2][0] = 0.2;  matrix[2][1] = 0.5;  matrix[2][2] = 1.0;  matrix[2][3] = 0.1;
    matrix[3][0] = 0.3;  matrix[3][1] = 0.3;  matrix[3][2] = 0.1;  matrix[3][3] = 1.0;

    printf("LD Matrix Scores:\n");
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            printf("%.2f | ", matrix[i][j]);
        }
        printf("\n");
    }

    // Create the heap
    MinHeap *heap = createMinHeap(n * w);

    // Store clusters for linking
    GeneCluster **allClusters = (GeneCluster**)malloc(sizeof(GeneCluster*) * n * w);
    int totalClusters = 0;

    // Insert clusters and store them
    printf("\nInitial gene clusters:\n");
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n && j - i <= w; j++) {
            int *genes = (int*)malloc(2 * sizeof(int));
            genes[0] = i + 1;
            genes[1] = j + 1;

            GeneCluster *cluster = createCluster(genes, 2, matrix[i][j]);
            allClusters[totalClusters++] = cluster;
            insertMinHeap(heap, cluster);
        }
    }

    // Link clusters properly
    for (int i = 0; i < totalClusters; i++) {
        if (i > 0)
            allClusters[i]->prev = allClusters[i - 1];
        if (i < totalClusters - 1)
            allClusters[i]->next = allClusters[i + 1];
        printCluster(allClusters[i]);
    }

    // Extraction example
    printf("\nExtracting cluster with lowest LD score:\n");
    GeneCluster *minCluster = extractMin(heap);
    printCluster(minCluster);

    // Final Heap state
    printf("\nRemaining clusters in heap:\n");
    for (int i = 0; i < heap->size; i++) {
        printCluster(heap->data[i]);
    }

    // Free memory
    freeMatrix(matrix, n);
    freeHeap(heap);
    free(allClusters);

    return 0;
}
