#pre_gwas_plink 1
# This script takes a VCF file as input and generates PLINK files (bim, bed, fam),
# computes linkage disequilibrium (LD) statistics, and creates a genotype matrix.
# It takes a VCF (.vcf) file as input and outputs a LD file (.ld)
#!/bin/bash
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <input.vcf>"
    exit 1
fi

ld_window=1000
ld_window_kb=1000
r2_threshold=0.00001

arg="$1"
new_arg="${arg/.vcf/}"

# Input and output parameters
echo ""
echo "Input VCF: $1";
echo "Output filename: $new_arg";
echo ""

echo "Computing  bim, bed, fam"
echo "------------------------"
echo ""
plink --vcf ./$1 --make-bed --out $new_arg

echo "Assigning unique IDs"
echo "------------------------"
echo ""
plink --bfile ./$new_arg --const-fid 0 --make-bed --out unique_ids

echo ""
echo "Computing LDs"
echo "-------------"
echo ""

echo "plink --bfile ./$new_arg --r2 --ld-window $ld_window --ld-window-kb $ld_window_kb --ld-window-r2 $r2_threshold --out ./$new_arg"
plink --bfile ./$new_arg --r2 --ld-window $ld_window --ld-window-kb $ld_window_kb --ld-window-r2 $r2_threshold --out ./$new_arg

echo ""
echo "Computing the genotype matrix (transpose)"
echo "-----------------------------------------"
echo ""

plink --bfile ./$new_arg --recode A-transpose --out ./$new_arg

exit 1
echo "Done!"