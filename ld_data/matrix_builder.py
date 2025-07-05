import pandas as pd

input_file = "./dataset_1/partition_1.ld"
output_file = "ld_sparse_matrix.tsv"

# Load the LD data (assumes whitespace-delimited, no header)
df = pd.read_csv(input_file, sep='\s+', comment='#')

# Extract needed columns
rows = df['BP_A'].astype(int)
cols = df['BP_B'].astype(int)
vals = df['R2']

# To keep symmetry, output both (i,j) and (j,i)
df_upper = pd.DataFrame({'row': rows, 'col': cols, 'value': vals})
df_lower = pd.DataFrame({'row': cols, 'col': rows, 'value': vals})

df_sparse = pd.concat([df_upper, df_lower], ignore_index=True)

# Save as tab-separated values (row, col, value)
df_sparse.to_csv(output_file, sep='\t', index=False, header=False)

print(f"Sparse matrix saved to {output_file}")
