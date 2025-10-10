import java.util.*;

class PuzzleState {
    int[][] board;
    int x, y;  // position of blank (0)
    String path;  // To store moves to reach the state

    public PuzzleState(int[][] board, int x, int y, String path) {
        this.board = new int[3][3];
        for (int i = 0; i < 3; i++)
            this.board[i] = board[i].clone();
        this.x = x;
        this.y = y;
        this.path = path;
    }

    // Generate a unique string for hashing
    public String getKey() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board)
            for (int num : row)
                sb.append(num);
        return sb.toString();
    }

    // Check if goal state is reached
    public boolean isGoal() {
        int[][] goal = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 0}
        };
        return Arrays.deepEquals(board, goal);
    }
}

public class EightPuzzleSolver {

    static int[] dx = {-1, 1, 0, 0};  // Up, Down, Left, Right moves
    static int[] dy = {0, 0, -1, 1};
    static char[] moveChar = {'U', 'D', 'L', 'R'};

    // BFS Implementation
    public static void bfs(int[][] startBoard, int startX, int startY) {
        Queue<PuzzleState> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        PuzzleState start = new PuzzleState(startBoard, startX, startY, "");
        queue.add(start);
        visited.add(start.getKey());

        while (!queue.isEmpty()) {
            PuzzleState current = queue.poll();

            if (current.isGoal()) {
                System.out.println("BFS Solution found! Moves: " + current.path);
                return;
            }

            for (int i = 0; i < 4; i++) {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];

                if (newX >= 0 && newX < 3 && newY >= 0 && newY < 3) {
                    int[][] newBoard = new int[3][3];
                    for (int r = 0; r < 3; r++)
                        newBoard[r] = current.board[r].clone();

                    // Swap blank with the adjacent number
                    newBoard[current.x][current.y] = newBoard[newX][newY];
                    newBoard[newX][newY] = 0;

                    PuzzleState neighbor = new PuzzleState(newBoard, newX, newY, current.path + moveChar[i]);
                    String key = neighbor.getKey();
                    if (!visited.contains(key)) {
                        visited.add(key);
                        queue.add(neighbor);
                    }
                }
            }
        }

        System.out.println("BFS: No solution found.");
    }

    // DFS Implementation (with depth limit to avoid infinite loops)
    public static void dfs(int[][] startBoard, int startX, int startY, int maxDepth) {
        Stack<PuzzleState> stack = new Stack<>();
        Set<String> visited = new HashSet<>();

        PuzzleState start = new PuzzleState(startBoard, startX, startY, "");
        stack.push(start);

        while (!stack.isEmpty()) {
            PuzzleState current = stack.pop();

            if (current.isGoal()) {
                System.out.println("DFS Solution found! Moves: " + current.path);
                return;
            }

            if (current.path.length() >= maxDepth)
                continue;

            visited.add(current.getKey());

            for (int i = 0; i < 4; i++) {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];

                if (newX >= 0 && newX < 3 && newY >= 0 && newY < 3) {
                    int[][] newBoard = new int[3][3];
                    for (int r = 0; r < 3; r++)
                        newBoard[r] = current.board[r].clone();

                    newBoard[current.x][current.y] = newBoard[newX][newY];
                    newBoard[newX][newY] = 0;

                    PuzzleState neighbor = new PuzzleState(newBoard, newX, newY, current.path + moveChar[i]);
                    String key = neighbor.getKey();
                    if (!visited.contains(key)) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        System.out.println("DFS: No solution found within depth limit.");
    }

    public static void main(String[] args) {
        int[][] initialBoard = {
            {1, 2, 3},
            {4, 0, 6},
            {7, 5, 8}
        };

        int blankX = 1, blankY = 1;  // Initial position of the blank (0)

        System.out.println("Starting BFS...");
        bfs(initialBoard, blankX, blankY);

        System.out.println("\nStarting DFS (max depth 20)...");
        dfs(initialBoard, blankX, blankY, 20);
    }
}
