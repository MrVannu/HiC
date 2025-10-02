#!/usr/bin/env python3
import argparse
import os
import pandas as pd
import numpy as np
from scipy.sparse import coo_matrix, save_npz

DEFAULT_INPUT = "./datasets/partition_1.ld"
DEFAULT_OUTPUT = "outputs/BASE_ld_upper"


def build_sparse_matrix(ld_file: str, output_prefix: str):
    # Define dtypes for efficient reading
    dtypes = {
        "CHR_A": "Int64",
        "BP_A": "Int64",
        "SNP_A": "string",
        "CHR_B": "Int64",
        "BP_B": "Int64",
        "SNP_B": "string",
        "R2": "float64"
    }

    print(f"Reading LD file: {ld_file}")
    df = pd.read_csv(
        ld_file,
        sep=r"\s+",
        header=0,
        comment="#",
        dtype=dtypes,
        low_memory=True
    )

    # Drop rows with missing data
    df = df.dropna(subset=["BP_A", "BP_B", "R2"])

    # Map unique positions to indices
    positions = np.sort(np.unique(np.concatenate((df["BP_A"].values, df["BP_B"].values))))
    pos_to_idx = {pos: idx for idx, pos in enumerate(positions)}
    n = len(positions)

    # Filter upper-triangular entries (i < j)
    i = np.array([pos_to_idx[bp] for bp in df["BP_A"]])
    j = np.array([pos_to_idx[bp] for bp in df["BP_B"]])
    data = df["R2"].values
    mask = i < j

    # Build sparse COO matrix
    sparse_ld = coo_matrix((data[mask], (i[mask], j[mask])), shape=(n, n))

    # Ensure output directory exists
    os.makedirs(os.path.dirname(output_prefix), exist_ok=True)

    # Save as TSV
    sparse_df = pd.DataFrame({
        "BP_A": [positions[x] for x in sparse_ld.row],
        "BP_B": [positions[y] for y in sparse_ld.col],
        "R2": sparse_ld.data
    })
    tsv_path = f"{output_prefix}.tsv"
    sparse_df.to_csv(tsv_path, sep="\t", index=False)

    # Save as compressed sparse format
    npz_path = f"{output_prefix}.npz"
    save_npz(npz_path, sparse_ld)

    print(f"Results saved to:\n  - {tsv_path}\n  - {npz_path}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Convert LD file into sparse matrix and TSV format.")
    parser.add_argument("input", nargs="?", default=DEFAULT_INPUT, help="Path to the input LD file")
    parser.add_argument("output", nargs="?", default=DEFAULT_OUTPUT, help="Output file prefix (without extension)")

    args = parser.parse_args()
    build_sparse_matrix(args.input, args.output)
