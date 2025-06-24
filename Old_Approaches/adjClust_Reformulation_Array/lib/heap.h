#ifndef HEAP_H
#define HEAP_H

#include <stdlib.h>
#include <stdio.h>

// Cluster Structure
typedef struct GeneCluster {
    int *genes;
    int size;
    double ld_score;
    struct GeneCluster *prev;
    struct GeneCluster *next;
} GeneCluster;

// Min Heap Structure
typedef struct {
    GeneCluster **data;  // Array of pointers
    int size;
    int capacity;
} MinHeap;


MinHeap* createMinHeap(int capacity);
void insertMinHeap(MinHeap *heap, GeneCluster *cluster);
GeneCluster* extractMin(MinHeap *heap);
void freeHeap(MinHeap *heap);

GeneCluster* createCluster(int *genes, int size, double ld_score);
GeneCluster* mergeClusters(GeneCluster *c1, GeneCluster *c2, double new_score);

#endif
