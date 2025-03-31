#ifndef HEAP_H
#define HEAP_H

// Struct to store gene pairs and their LD scores
typedef struct {
    int gene1;
    int gene2;
    double ld_score;
} GenePair;

// Struct for Min Heap
typedef struct {
    GenePair *data;  // Dynamic array of GenePair
    int size;        // Number of elements in the heap
    int capacity;    // Max dimension of the heap
} MinHeap;

// Function declarations
MinHeap* createMinHeap(int capacity);
void insertMinHeap(MinHeap *heap, int gene1, int gene2, double ld_score);
GenePair extractMin(MinHeap *heap);
void loadHeapFromMatrix(MinHeap *heap, double **matrix, int n, int h);
void freeHeap(MinHeap *heap);

#endif
