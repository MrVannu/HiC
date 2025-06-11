import itertools
import numpy as np

A = [0, 1, 2]
B = [1, 2, 3]

input_matrix = np.array([
    [1, 2, 3, 4],
    [5, 6, 7, 8],
    [9, 10, 11, 12],
    [13, 14, 15, 16]
])


pairs = list(itertools.product(A, B))
total = sum(input_matrix[a][b] for a, b in pairs)
average = total / len(pairs)

print(f"Metric output is: {average}")