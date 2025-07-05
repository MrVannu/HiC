#include <iostream>
#include <vector>
#include <iomanip> 

// Function: reverse row cumulative sums
std::vector<std::vector<double>> matR_full_rowCumsums(
    const std::vector<std::vector<double>>& Csq, 
    const int h, 
    int nthreads = 1
) {
    int p = Csq.size();
    std::vector<std::vector<double>> out(p, std::vector<double>(h+1, 0.0));

    for (int i = 0; i < p; i++) {
        int k = 0;
        double value;

        for (int j = i; j >= std::max(i-h, 0); j--) {
            value = Csq[i][j];

            if (k == 0) {
                out[p-i-1][k] = value;
            } else {
                out[p-i-1][k] = out[p-i-1][k-1] + 2.0 * value;
            }
            k++;
        }

        while (k < h+1) {
            out[p-i-1][k] = out[p-i-1][k-1];
            k++;
        }
    }

    return out;
}


void printMatrix(const std::vector<std::vector<double>>& mat) {
    for (const auto& row : mat) {
        for (double val : row) {
            std::cout << std::setw(8) << val << " ";
        }
        std::cout << "\n";
    }
}

int main() {
    std::vector<std::vector<double>> Csq = {
        {1.0, 2.0, 3.0, 3.0, 3.0},
        {3.0, 1.0, 2.0, 3.0, 3.0},
        {3.0, 3.0, 1.0, 2.0, 3.0},
        {3.0, 3.0, 3.0, 1.0, 2.0},
        {3.0, 3.0, 3.0, 3.0, 1.0}
    };

    std::cout << "Input Matrix (Csq):\n";
    printMatrix(Csq);

    int h = 2; // number of steps back

    std::vector<std::vector<double>> result = matR_full_rowCumsums(Csq, h);

    std::cout << "\nOutput Matrix:\n";
    printMatrix(result);

    return 0;
}
