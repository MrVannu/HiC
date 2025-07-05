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
