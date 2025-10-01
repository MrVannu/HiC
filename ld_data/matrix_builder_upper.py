import pandas as pd
import numpy as np

ld_file = "./dataset_1/partition_1.ld"

dtypes = {
    "CHR_A": "Int64",
    "BP_A": "Int64",
    "SNP_A": "string",
    "CHR_B": "Int64",
    "BP_B": "Int64",
    "SNP_B": "string",
    "R2": "float64"
}

df = pd.read_csv(
    ld_file,
    sep=r"\s+",
    header=0,
    comment="#",
    dtype=dtypes,
    low_memory=False
)

df = df.dropna(subset=["BP_A", "BP_B", "R2"])

positions = np.sort(np.unique(np.concatenate((df["BP_A"].values, df["BP_B"].values))))
pos_to_idx = {pos: idx for idx, pos in enumerate(positions)}

# Keep only upper triangular (i < j)
filtered = []
for _, row in df.iterrows():
    i = pos_to_idx[row["BP_A"]]
    j = pos_to_idx[row["BP_B"]]
    if i < j:  # upper triangular without diagonal
        filtered.append((row["BP_A"], row["BP_B"], row["R2"]))

filtered_df = pd.DataFrame(filtered, columns=["BP_A", "BP_B", "R2"])
filtered_df.to_csv("outputs/BASE_ld_upper_matrix.tsv", sep="\t", index=False)
