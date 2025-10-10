import java.util.*;

class Node implements Comparable<Node> {
    int x, y;
    int gCost, hCost;
    Node parent;

    public Node(int x, int y, int gCost, int hCost, Node parent) {
        this.x = x;
        this.y = y;
        this.gCost = gCost;
        this.hCost = hCost;
        this.parent = parent;
    }

    public int fCost() {
        return gCost + hCost;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.fCost(), other.fCost());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) return false;
        Node other = (Node) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

public class AStarPathfinding {

    private static final int[][] directions = {
        { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }
    };

    private static int heuristic(int x1, int y1, int x2, int y2) {
        // Manhattan Distance
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public static List<Node> aStar(int[][] grid, int startX, int startY, int goalX, int goalY) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();

        Node startNode = new Node(startX, startY, 0, heuristic(startX, startY, goalX, goalY), null);
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            if (current.x == goalX && current.y == goalY) {
                // Goal reached: reconstruct path
                List<Node> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = current.parent;
                }
                Collections.reverse(path);
                return path;
            }

            closedSet.add(current);

            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                if (nx < 0 || ny < 0 || nx >= grid.length || ny >= grid[0].length) continue;
                if (grid[nx][ny] == 1) continue;  // 1 = obstacle

                Node neighbor = new Node(nx, ny, current.gCost + 1,
                        heuristic(nx, ny, goalX, goalY), current);

                if (closedSet.contains(neighbor)) continue;

                openList.add(neighbor);
            }
        }

        return null; // No path found
    }

    public static void main(String[] args) {
        int[][] grid = {
            { 0, 0, 0, 0 },
            { 0, 1, 1, 0 },
            { 0, 0, 0, 0 },
            { 0, 1, 0, 0 }
        };

        int startX = 0, startY = 0;
        int goalX = 3, goalY = 3;

        List<Node> path = aStar(grid, startX, startY, goalX, goalY);

        if (path != null) {
            System.out.println("Path found:");
            for (Node node : path) {
                System.out.println("(" + node.x + ", " + node.y + ")");
            }
        } else {
            System.out.println("No path found.");
        }
    }
}
