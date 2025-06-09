import numpy as np

def sort_median(input_matrix):
    input_matrix = np.array(input_matrix)
    
    cols_median = np.median(input_matrix, axis=0)
    ordered_index = np.argsort(-cols_median)
    
    ordered_matrix = input_matrix[:, ordered_index]
    return ordered_matrix.tolist(), ordered_index.tolist()


# Sandbox example
input_matrix = [
    [3, 1, 4],
    [6, 5, 9],
    [2, 7, 8]
]

ordered_matrix, ordered_indices = sort_median(input_matrix)

print("Ordered matrix:")
for row in ordered_matrix:
    print(row)

print("\nOriginal Order:", ordered_indices)
