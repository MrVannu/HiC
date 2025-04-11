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

// Calulates LD score (simulation purposes)
double computeAverageLD(int *genes, int size, double **matrix) {
    double sum = 0.0;
    int count = 0;
    for (int i = 0; i < size; i++) {
        for (int j = i + 1; j < size; j++) {
            sum += matrix[genes[i] - 1][genes[j] - 1]; // -1 per 1-based
            count++;
        }
    }
    return count > 0 ? sum / count : 0.0;
}

// Expands a cluster by merging it with prev and next, if existing
void expandClusterCandidates(MinHeap *heap, GeneCluster *minCluster, double **matrix) {
    GeneCluster *prev = minCluster->prev;
    GeneCluster *next = minCluster->next;

    printf("\n[EXPAND] Cluster estratto:\n");
    printCluster(minCluster);

    if (prev) {
        printf("[EXPAND] Unione con PREV:\n");
        printCluster(prev);

        // Prev + minCluster
        int new_size = prev->size + minCluster->size;
        int *genes = (int*)malloc(sizeof(int) * new_size);
        for (int i = 0; i < prev->size; i++) genes[i] = prev->genes[i];
        for (int i = 0; i < minCluster->size; i++) genes[prev->size + i] = minCluster->genes[i];

        double ld = computeAverageLD(genes, new_size, matrix);
        GeneCluster *merged = createCluster(genes, new_size, ld);

        // Link in the list
        merged->prev = prev->prev;
        merged->next = minCluster->next;
        if (merged->prev) merged->prev->next = merged;
        if (merged->next) merged->next->prev = merged;

        insertMinHeap(heap, merged);
        printf("[EXPAND] Inserito cluster PREV+MIN:\n");
        printCluster(merged);
    }
    else printf("Prev was NULL\n");


    if (next) {
        printf("[EXPAND] Unione con NEXT:\n");
        printCluster(next);

        // Union minCluster + next
        int new_size = minCluster->size + next->size;
        int *genes = (int*)malloc(sizeof(int) * new_size);
        for (int i = 0; i < minCluster->size; i++) genes[i] = minCluster->genes[i];
        for (int i = 0; i < next->size; i++) genes[minCluster->size + i] = next->genes[i];

        double ld = computeAverageLD(genes, new_size, matrix);
        GeneCluster *merged = createCluster(genes, new_size, ld);

        // Link in the list
        merged->prev = minCluster->prev;
        merged->next = next->next;
        if (merged->prev) merged->prev->next = merged;
        if (merged->next) merged->next->prev = merged;

        insertMinHeap(heap, merged);
        printf("[EXPAND] Inserito cluster MIN+NEXT:\n");
        printCluster(merged);
    } 
    else printf("Next was NULL\n");

    // Removes the minCluster from the list
    if (prev) prev->next = next;
    if (next) next->prev = prev;
    free(minCluster->genes);
    free(minCluster);
}

int main() {
    int n = 4;
    int w = 1; // Always == 1 for adjClust

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

    // Create Heap and initial clusters
    MinHeap *heap = createMinHeap(n * w);
    GeneCluster **allClusters = (GeneCluster**)malloc(sizeof(GeneCluster*) * n * w);
    int totalClusters = 0;

    printf("\n[INIT] Initial clusters:\n");
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

    // Link prev/next
    for (int i = 0; i < totalClusters; i++) {
        if (i > 0) allClusters[i]->prev = allClusters[i - 1];
        if (i < totalClusters - 1) allClusters[i]->next = allClusters[i + 1];
        printCluster(allClusters[i]);
    }

    // Extraction example
    printf("\n[STEP] Estrazione e fusione:\n");
    GeneCluster *minCluster = extractMin(heap);
    expandClusterCandidates(heap, minCluster, matrix);

    // Final Heap state
    printf("\n[FINAL] Heap attuale:\n");
    for (int i = 0; i < heap->size; i++) {
        printCluster(heap->data[i]);
    }

    // Free memory
    freeMatrix(matrix, n);
    freeHeap(heap);
    free(allClusters);

    return 0;
}
