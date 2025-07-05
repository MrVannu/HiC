import argparse
import os

DEFAULT_PATH = "/home/ubulunux/HiC/raw_data/gemma_data/example_1/example_1.vcf" 
DEFAULT_SIZE = 1000
DEFAULT_NUM_PARTITIONS = 10

def partition_vcf(file_path: str, rows_per_partition: int, number_partitions: int):
    # Read file and split into header and data
    with open(file_path, 'r') as file:
        header = []
        data = []
        for line in file:
            if line.startswith("#"):
                header.append(line)
            else:
                data.append(line)

    total_rows = len(data)
    total_partitions = (total_rows + rows_per_partition - 1) // rows_per_partition
    base_dir = os.path.dirname(file_path)

    for i in range(min(number_partitions, total_partitions)):
        start = i * rows_per_partition
        end = start + rows_per_partition
        partition_data = data[start:end]
        partition_filename = os.path.join(base_dir, f"partition_{i+1}.vcf")

        with open(partition_filename, 'w') as out_file:
            out_file.writelines(header)
            out_file.writelines(partition_data)

        print(f"Created: {partition_filename} with {len(partition_data)} records")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Partition a VCF file into chunks with header.")
    parser.add_argument("file", nargs="?", default=DEFAULT_PATH, help="Path to the input VCF file")
    parser.add_argument("rows", nargs="?", type=int, default=DEFAULT_SIZE, help="Number of data rows per partition")
    parser.add_argument("number_partitions", nargs="?", type=int, default=DEFAULT_NUM_PARTITIONS, help="Number of partitions to create")

    args = parser.parse_args()
    partition_vcf(args.file, args.rows, args.number_partitions)
