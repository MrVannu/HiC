#include <iostream>
#include <vector>
#include <iomanip>

// Function: full matrix row cumulative sums
std::vector<std::vector<double>> matL_full_rowCumsums(
    const std::vector<std::vector<double>>& Csq, 
    const int h, 
    int nthreads = 1
) {
    int p = Csq.size();
    std::vector<std::vector<double>> out(p, std::vector<double>(h+1, 0.0));

    for (int i = 0; i < p; i++) {
        int k = 0;
        double value;

        for (int j = i; j < std::min(i+h+1, p); j++) {
            value = Csq[i][j];

            if (k == 0) {
                out[i][k] = value;
            } else {
                out[i][k] = out[i][k-1] + 2.0 * value;
            }
            k++;
        }

        while (k < h+1) {
            out[i][k] = out[i][k-1];
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
        {1.0, 2.0, 3.0, 3.0, 12.0},
        {3.0, 1.0, 2.0, 3.0, 3.0},
        {3.0, 3.0, 1.0, 2.0, 3.0},
        {3.0, 3.0, 3.0, 1.0, 2.0},
        {3.0, 3.0, 3.0, 3.0, 1.0}
    };

    std::cout << "Input Matrix (Csq):\n";
    printMatrix(Csq);

    int h = 2; // how many columns ahead

    std::vector<std::vector<double>> result = matL_full_rowCumsums(Csq, h);

    std::cout << "\nOutput Matrix:\n";
    printMatrix(result);

    return 0;
}
