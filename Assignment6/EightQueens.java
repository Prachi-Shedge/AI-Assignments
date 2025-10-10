import java.util.*;

public class EightQueens {

    static final int N = 8;

    // Function to print the chessboard
    static void printSolution(int board[][]) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)
                System.out.print((board[i][j] == 1 ? "Q " : ". "));
            System.out.println();
        }
        System.out.println();
    }

    // Check if a queen can be placed on board[row][col]
    static boolean isSafe(int board[][], int row, int col) {
        // Check this row on left side
        for (int i = 0; i < col; i++)
            if (board[row][i] == 1)
                return false;

        // Check upper diagonal on left side
        for (int i = row, j = col; i >= 0 && j >= 0; i--, j--)
            if (board[i][j] == 1)
                return false;

        // Check lower diagonal on left side
        for (int i = row, j = col; j >= 0 && i < N; i++, j--)
            if (board[i][j] == 1)
                return false;

        return true;
    }

    // Solve the N Queens problem using backtracking
    static boolean solveNQUtil(int board[][], int col) {
        if (col >= N) {
            printSolution(board);
            return true;
        }

        boolean res = false;
        for (int i = 0; i < N; i++) {
            if (isSafe(board, i, col)) {
                board[i][col] = 1;

                // Recur to place rest of the queens
                res = solveNQUtil(board, col + 1) || res;

                // Backtrack
                board[i][col] = 0;
            }
        }
        return res;
    }

    // Main solver
    static void solveNQ() {
        int board[][] = new int[N][N];

        if (!solveNQUtil(board, 0)) {
            System.out.println("Solution does not exist");
        }
    }

    public static void main(String args[]) {
        solveNQ();
    }
}
