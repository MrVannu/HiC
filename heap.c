#include "lib/heap.h"

// Create a new cluster
GeneCluster* createCluster(int *genes, int size, double ld_score) {
    GeneCluster *cluster = (GeneCluster*)malloc(sizeof(GeneCluster));
    cluster->genes = (int*)malloc(size * sizeof(int));
    for (int i = 0; i < size; i++) {
        cluster->genes[i] = genes[i];
    }
    cluster->size = size;
    cluster->ld_score = ld_score;
    cluster->prev = NULL;
    cluster->next = NULL;
    return cluster;
}

// Merge two clusters
GeneCluster* mergeClusters(GeneCluster *c1, GeneCluster *c2, double new_score) {
    int new_size = c1->size + c2->size;
    int *new_genes = (int*)malloc(new_size * sizeof(int));

    for (int i = 0; i < c1->size; i++)
        new_genes[i] = c1->genes[i];
    for (int i = 0; i < c2->size; i++)
        new_genes[c1->size + i] = c2->genes[i];

    GeneCluster *new_cluster = createCluster(new_genes, new_size, new_score);
    new_cluster->prev = c1;
    new_cluster->next = c2;

    return new_cluster;
}

// Create the Min Heap
MinHeap* createMinHeap(int capacity) {
    MinHeap *heap = (MinHeap*)malloc(sizeof(MinHeap));
    heap->data = (GeneCluster**)malloc(capacity * sizeof(GeneCluster*));
    heap->size = 0;
    heap->capacity = capacity;
    return heap;
}

// Heapify Up
void heapifyUp(MinHeap *heap, int index) {
    int parent = (index - 1) / 2;
    while (index > 0 && heap->data[index]->ld_score < heap->data[parent]->ld_score) {
        GeneCluster *temp = heap->data[index];
        heap->data[index] = heap->data[parent];
        heap->data[parent] = temp;
        index = parent;
    }
}

// Inserts a cluster into the Heap
void insertMinHeap(MinHeap *heap, GeneCluster *cluster) {
    if (heap->size >= heap->capacity) {
        printf("Heap piena!\n");
        return;
    }
    heap->data[heap->size] = cluster;
    heapifyUp(heap, heap->size);
    heap->size++;
}

// Heapify Down
void heapifyDown(MinHeap *heap, int index) {
    int left = 2 * index + 1;
    int right = 2 * index + 2;
    int smallest = index;

    if (left < heap->size && heap->data[left]->ld_score < heap->data[smallest]->ld_score)
        smallest = left;

    if (right < heap->size && heap->data[right]->ld_score < heap->data[smallest]->ld_score)
        smallest = right;

    if (smallest != index) {
        GeneCluster *temp = heap->data[index];
        heap->data[index] = heap->data[smallest];
        heap->data[smallest] = temp;
        heapifyDown(heap, smallest);
    }
}

// Extracts the min LD-score cluster
GeneCluster* extractMin(MinHeap *heap) {
    if (heap->size <= 0) {
        printf("EMPTY Heap!\n");
        return NULL;
    }
    
    heapifyDown(heap, 0);
    GeneCluster *min = heap->data[0];
    heap->data[0] = heap->data[heap->size - 1];
    heap->size--;

    return min;
}

// Free up Heap memory
void freeHeap(MinHeap *heap) {
    for (int i = 0; i < heap->size; i++) {
        free(heap->data[i]->genes);
        free(heap->data[i]);
    }
    free(heap->data);
    free(heap);
}
