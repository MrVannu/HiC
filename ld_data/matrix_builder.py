import pandas as pd
import numpy as np

ld_file = "./dataset_1/partition_1.ld"

cols = ["CHR_A", "BP_A", "SNP_A", "CHR_B", "BP_B", "SNP_B", "R2"]
df = pd.read_csv(ld_file, sep="\s+", names=cols, comment="#")

# Convert BP_A and BP_B to numeric
df['BP_A'] = pd.to_numeric(df['BP_A'], errors='coerce')
df['BP_B'] = pd.to_numeric(df['BP_B'], errors='coerce')

# Drop rows where BP_A or BP_B is NaN (invalid)
df = df.dropna(subset=['BP_A', 'BP_B'])

df['BP_A'] = df['BP_A'].astype(int)
df['BP_B'] = df['BP_B'].astype(int)

positions = np.sort(np.unique(np.concatenate((df['BP_A'].values, df['BP_B'].values))))
pos_to_idx = {pos: idx for idx, pos in enumerate(positions)}

matrix = np.zeros((len(positions), len(positions)))
np.fill_diagonal(matrix, 1)

for _, row in df.iterrows():
    i = pos_to_idx[row['BP_A']]
    j = pos_to_idx[row['BP_B']]
    r2 = row['R2']
    matrix[i, j] = r2
    matrix[j, i] = r2

matrix_df = pd.DataFrame(matrix, index=positions, columns=positions)
matrix_df.to_csv("outputs/BASE_ld_matix.tsv", sep="\t")