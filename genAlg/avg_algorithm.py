import numpy as np

def sort_avg(input_matrix):
    input_matrix = np.array(input_matrix)

    cols_avg = input_matrix.mean(axis=0)
    ordered_index = np.argsort(-cols_avg)

    ordered_matrix = input_matrix[:, ordered_index]

    return ordered_matrix.tolist(), ordered_index.tolist()

# Sandbox example
input_matrix = [
    [3, 1, 4],
    [6, 50, 9],
    [2, 7, 8]
]

#input_matrix = np.loadtxt("similarity_matrix.csv", delimiter=",")

ordered_matrix, original_index = sort_avg(input_matrix)

print("Output Matrix:")
for row in ordered_matrix:
    print(row)

print("\nOriginal Order:", original_index)
