import numpy as np
import sys

def is_symmetric(matrix, tol=1e-8):
    return np.allclose(matrix, matrix.T, atol=tol)

def read_tsv_matrix(file_path):
    # Load numeric matrix from TSV file
    return np.loadtxt(file_path, delimiter='\t')

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python check_symmetric.py <input_matrix.tsv>")
        sys.exit(1)

    input_file = sys.argv[1]
    matrix = read_tsv_matrix(input_file)

    symmetric = is_symmetric(matrix)

    print(f"Matrix is symmetric: {symmetric}")
