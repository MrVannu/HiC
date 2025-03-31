#include <stdio.h>
#include <stdlib.h>
#include "lib/heap.h"

// Creates a new Min Heap with the given capacity
MinHeap* createMinHeap(int capacity) {
    MinHeap *heap = (MinHeap*)malloc(sizeof(MinHeap));
    heap->data = (GenePair*)malloc(capacity * sizeof(GenePair));
    heap->size = 0;
    heap->capacity = capacity;
    return heap;
}

// Swap function to swap two GenePair elements in the heap
void swap(GenePair *a, GenePair *b) {
    GenePair temp = *a;
    *a = *b;
    *b = temp;
}

// Heapify Up (used to balance the heap after insertion to keep the min balanced)
void heapifyUp(MinHeap *heap, int index) {
    int parent = (index - 1) / 2;
    while (index > 0 && heap->data[index].ld_score < heap->data[parent].ld_score) {
        swap(&heap->data[index], &heap->data[parent]);
        index = parent;
    }
}

// Inserts an element into the Min Heap
void insertMinHeap(MinHeap *heap, int gene1, int gene2, double ld_score) {
    if (heap->size >= heap->capacity) {
        printf("Errore: Heap piena!\n");
        return;
    }
    heap->data[heap->size].gene1 = gene1;
    heap->data[heap->size].gene2 = gene2;
    heap->data[heap->size].ld_score = ld_score;
    heapifyUp(heap, heap->size);
    heap->size++;
}

// Heapify Down (used after extracting the minimum to keep the heap balanced)
void heapifyDown(MinHeap *heap, int index) {
    int left = 2 * index + 1;
    int right = 2 * index + 2;
    int smallest = index;

    if (left < heap->size && heap->data[left].ld_score < heap->data[smallest].ld_score)
        smallest = left;
    
    if (right < heap->size && heap->data[right].ld_score < heap->data[smallest].ld_score)
        smallest = right;

    if (smallest != index) {
        swap(&heap->data[index], &heap->data[smallest]);
        heapifyDown(heap, smallest);
    }
}

// Function to get the Root (min element) of the Min Heap
GenePair extractMin(MinHeap *heap) {
    if (heap->size <= 0) {
        printf("Heap vuota!\n");
        return (GenePair){-1, -1, -1};  // If an error occurs, return an invalid GenePair
    }
    GenePair min = heap->data[0];
    heap->data[0] = heap->data[heap->size - 1];
    heap->size--;
    heapifyDown(heap, 0);
    return min;
}

// Loads data into the heap structure from the LD score matrix
void loadHeapFromMatrix(MinHeap *heap, double **matrix, int n, int h) {
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n && j - i <= h; j++) {
            insertMinHeap(heap, i + 1, j + 1, matrix[i][j]);  // 1-based
        }
    }
}

// Frees memory allocated for the heap
void freeHeap(MinHeap *heap) {
    free(heap->data);
    free(heap);
}
