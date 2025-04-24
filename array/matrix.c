#include <stdio.h>
#include <stdlib.h>
#include "lib/matrix.h"

// Allocates memory for a square matrix of size n x n
double** createMatrix(int n) {
    double **matrix = (double **)malloc(n * sizeof(double *));
    for (int i = 0; i < n; i++) {
        matrix[i] = (double *)malloc(n * sizeof(double));
    }
    return matrix;
}

// Frees memory allocated for the matrix
void freeMatrix(double **matrix, int n) {
    for (int i = 0; i < n; i++) {
        free(matrix[i]);
    }
    free(matrix);
}
