import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class MazeSolverGUI extends JFrame {
    private static final int SIZE = 20;
    private static final int CELL_SIZE = 25;
    private Cell[][] grid = new Cell[SIZE][SIZE];
    private JPanel mazePanel;
    private Cell start = null, end = null;

    public MazeSolverGUI() {
        setTitle("Maze Solver - DFS & BFS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(SIZE * CELL_SIZE + 200, SIZE * CELL_SIZE + 50);
        setLayout(new BorderLayout());

        mazePanel = new JPanel(new GridLayout(SIZE, SIZE));
        initializeGrid();
        add(mazePanel, BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.EAST);

        generateMaze();
        setVisible(true);
    }

    private void initializeGrid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = new Cell(row, col);
                cell.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            if (start != null) start.setType("empty");
                            start = cell;
                            cell.setType("start");
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            if (end != null) end.setType("empty");
                            end = cell;
                            cell.setType("end");
                        }
                    }
                });
                grid[row][col] = cell;
                mazePanel.add(cell);
            }
        }
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 5, 5));

        JButton btnDFS = new JButton("Solve with DFS");
        btnDFS.addActionListener(e -> solveMaze("DFS"));
        panel.add(btnDFS);

        JButton btnBFS = new JButton("Solve with BFS");
        btnBFS.addActionListener(e -> solveMaze("BFS"));
        panel.add(btnBFS);

        JButton btnGenerate = new JButton("Generate New Maze");
        btnGenerate.addActionListener(e -> generateMaze());
        panel.add(btnGenerate);

        return panel;
    }

    private void generateMaze() {
        Random rand = new Random();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (rand.nextDouble() < 0.3) {
                    grid[row][col].setType("wall");
                } else {
                    grid[row][col].setType("empty");
                }
            }
        }
        if (start != null) start.setType("empty");
        if (end != null) end.setType("empty");
        start = end = null;
    }

    private void solveMaze(String algorithm) {
        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this, "Please select both start and end points.");
            return;
        }

        resetPath();

        boolean pathFound = false;

        if (algorithm.equals("BFS")) {
            pathFound = bfs(start, end);
        } else {
            pathFound = dfs(start, end);
        }

        if (pathFound) {
            JOptionPane.showMessageDialog(this, "Path Found!");
        } else {
            JOptionPane.showMessageDialog(this, "No path exists!");
        }
    }

    private void resetPath() {
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                if (cell.type.equals("path") || cell.type.equals("visited")) {
                    cell.setType("empty");
                }
            }
        }
    }

    private List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int r = cell.row, c = cell.col;

        if (r > 0) neighbors.add(grid[r - 1][c]);
        if (r < SIZE - 1) neighbors.add(grid[r + 1][c]);
        if (c > 0) neighbors.add(grid[r][c - 1]);
        if (c < SIZE - 1) neighbors.add(grid[r][c + 1]);

        return neighbors;
    }

    private boolean bfs(Cell start, Cell end) {
        Queue<Cell> queue = new LinkedList<>();
        Map<Cell, Cell> parent = new HashMap<>();
        Set<Cell> visited = new HashSet<>();

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            if (current.equals(end)) {
                reconstructPath(parent, end);
                return true;
            }

            for (Cell neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor) && !neighbor.type.equals("wall")) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    if (!neighbor.equals(end)) neighbor.setType("visited");
                }
            }
        }

        return false;
    }

    private boolean dfs(Cell start, Cell end) {
        Stack<Cell> stack = new Stack<>();
        Map<Cell, Cell> parent = new HashMap<>();
        Set<Cell> visited = new HashSet<>();

        stack.push(start);
        visited.add(start);

        while (!stack.isEmpty()) {
            Cell current = stack.pop();
            if (current.equals(end)) {
                reconstructPath(parent, end);
                return true;
            }

            for (Cell neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor) && !neighbor.type.equals("wall")) {
                    stack.push(neighbor);
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    if (!neighbor.equals(end)) neighbor.setType("visited");
                }
            }
        }

        return false;
    }

    private void reconstructPath(Map<Cell, Cell> parent, Cell end) {
        Cell current = end;
        while (parent.containsKey(current) && !current.equals(start)) {
            current = parent.get(current);
            if (!current.equals(start)) {
                current.setType("path");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MazeSolverGUI::new);
    }

    // Inner class for each cell in the grid
    static class Cell extends JPanel {
        int row, col;
        String type = "empty"; // empty, wall, start, end, path, visited

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            setBackground(Color.WHITE);
        }

        public void setType(String type) {
            this.type = type;
            switch (type) {
                case "wall" -> setBackground(Color.BLACK);
                case "start" -> setBackground(Color.GREEN);
                case "end" -> setBackground(Color.RED);
                case "path" -> setBackground(Color.YELLOW);
                case "visited" -> setBackground(Color.CYAN);
                default -> setBackground(Color.WHITE);
            }
            repaint();
        }
    }
}
// Q.5.a answer