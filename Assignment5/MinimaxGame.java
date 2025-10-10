public class MinimaxGame {

    // Minimax function
    public static int minimax(int number, boolean isMaximizingPlayer) {
        // Base case: if number is 0, last player won
        if (number == 0) {
            return isMaximizingPlayer ? -1 : +1;
        }

        if (isMaximizingPlayer) {
            int bestScore = Integer.MIN_VALUE;

            // Try all moves: subtract 1, 2, or 3
            for (int move = 1; move <= 3; move++) {
                if (number - move >= 0) {
                    int score = minimax(number - move, false);
                    bestScore = Math.max(bestScore, score);
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;

            for (int move = 1; move <= 3; move++) {
                if (number - move >= 0) {
                    int score = minimax(number - move, true);
                    bestScore = Math.min(bestScore, score);
                }
            }
            return bestScore;
        }
    }

    // Find the best move for the current player
    public static int findBestMove(int number) {
        int bestMove = -1;
        int bestScore = Integer.MIN_VALUE;

        for (int move = 1; move <= 3; move++) {
            if (number - move >= 0) {
                int score = minimax(number - move, false);
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        }

        return bestMove;
    }

    public static void main(String[] args) {
        int startingNumber = 10;

        System.out.println("Initial Number: " + startingNumber);
        int bestMove = findBestMove(startingNumber);
        System.out.println("Best first move is to subtract: " + bestMove);
    }
}
