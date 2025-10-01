import argparse
import os

DEFAULT_PATH = "/home/ubulunux/HiC/raw_data/gemma_data/dataset_1/example_1.vcf" 
DEFAULT_SIZE = 1000
DEFAULT_NUM_PARTITIONS = 10

def partition_vcf(file_path: str, rows_per_partition: int, number_partitions: int):
    base_dir = os.path.dirname(file_path)
    partition_idx = 1
    rows_written = 0

    # Open first partition file
    out_file = None

    with open(file_path, 'r') as file:
        header_lines = []
        for line in file:
            if line.startswith("#"):
                header_lines.append(line)
                continue

            if rows_written % rows_per_partition == 0:
                if out_file:
                    out_file.close()
                if partition_idx > number_partitions:
                    break
                partition_filename = os.path.join(base_dir, f"partition_{partition_idx}.vcf")
                out_file = open(partition_filename, 'w')
                out_file.writelines(header_lines)
                print(f"Creating: {partition_filename}")
                partition_idx += 1

            out_file.write(line)
            rows_written += 1

    if out_file:
        out_file.close()

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Partition a VCF file into chunks with header.")
    parser.add_argument("file", nargs="?", default=DEFAULT_PATH, help="Path to the input VCF file")
    parser.add_argument("rows", nargs="?", type=int, default=DEFAULT_SIZE, help="Number of data rows per partition")
    parser.add_argument("number_partitions", nargs="?", type=int, default=DEFAULT_NUM_PARTITIONS, help="Number of partitions to create")

    args = parser.parse_args()
    partition_vcf(args.file, args.rows, args.number_partitions)
