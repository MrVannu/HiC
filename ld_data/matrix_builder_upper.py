import pandas as pd
import numpy as np
from scipy.sparse import coo_matrix
from scipy.sparse import save_npz

# Input LD file
ld_file = "./dataset_1/partition_1.ld"

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

# Read LD file
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

# Save as TSV (only non-zero entries)
sparse_df = pd.DataFrame({
    "BP_A": [positions[i] for i in sparse_ld.row],
    "BP_B": [positions[j] for j in sparse_ld.col],
    "R2": sparse_ld.data
})
sparse_df.to_csv("outputs/BASE_ld_upper.tsv", sep="\t", index=False)

# Save as compressed sparse format
save_npz("outputs/BASE_ld_upper_matrix.npz", sparse_ld)
