import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class FloodItSimonStyle extends JFrame {

    /* ================= CONFIG ================= */

    Color[] ALL_COLORS = {
            Color.RED, Color.GREEN, Color.BLUE,
            Color.YELLOW, Color.MAGENTA, Color.ORANGE
    };

    String[] COLOR_NAMES = {"Red", "Green", "Blue", "Yellow", "Magenta", "Orange"};

    int SIZE = 10;
    int COLOR_COUNT = 6;
    int CELL = 35;

    int[][] grid;
    int moves = 0;

    boolean vsBot = false;
    boolean playerTurn = true;
    boolean gameOver = false;

    Stack<int[][]> undo = new Stack<>();
    Stack<int[][]> redo = new Stack<>();

    Board board;

    JLabel movesLabel;
    JLabel hintLabel;

    JComboBox<String> modeBox;
    JComboBox<Integer> sizeBox;
    JComboBox<Integer> colorBox;

    JButton undoBtn, redoBtn;

    final String TEAM_NAME = "Alpha Coders";

    /* ================= HELPER CLASS ================= */

    class ColorScore {
        int color;
        int score;

        ColorScore(int color, int score) {
            this.color = color;
            this.score = score;
        }
    }

    /* ================= CONSTRUCTOR ================= */

    public FloodItSimonStyle() {
        setTitle("Flood-It");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createToolbar();
        createCenterBoard();
        createBottomPanel();

        newGame();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /* ================= TOOLBAR ================= */

    private void createToolbar() {
        JPanel bar = new JPanel();

        modeBox = new JComboBox<>(new String[]{"Single Player", "Player vs Bot"});

        sizeBox = new JComboBox<>();
        for (int i = 4; i <= 14; i++) sizeBox.addItem(i);

        colorBox = new JComboBox<>();
        for (int i = 2; i <= 6; i++) colorBox.addItem(i);

        JButton newBtn = new JButton("New Game");
        undoBtn = new JButton("Undo");
        redoBtn = new JButton("Redo");
        JButton hintBtn = new JButton("Hint");

        newBtn.addActionListener(e -> newGame());
        undoBtn.addActionListener(e -> undoMove());
        redoBtn.addActionListener(e -> redoMove());
        hintBtn.addActionListener(e -> showHint());

        bar.add(new JLabel("Mode"));
        bar.add(modeBox);
        bar.add(new JLabel("Size"));
        bar.add(sizeBox);
        bar.add(new JLabel("Colors"));
        bar.add(colorBox);
        bar.add(newBtn);
        bar.add(undoBtn);
        bar.add(redoBtn);
        bar.add(hintBtn);

        add(bar, BorderLayout.NORTH);
    }


    private void createCenterBoard() {
        board = new Board();
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(board);
        add(wrapper, BorderLayout.CENTER);
    }


    private void createBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());

        movesLabel = new JLabel("", JLabel.CENTER);
        movesLabel.setFont(new Font("Arial", Font.BOLD, 16));

        hintLabel = new JLabel(" ", JLabel.CENTER);

        bottom.add(movesLabel, BorderLayout.CENTER);
        bottom.add(hintLabel, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);
    }


    private void newGame() {
        SIZE = (int) sizeBox.getSelectedItem();
        COLOR_COUNT = (int) colorBox.getSelectedItem();
        vsBot = modeBox.getSelectedIndex() == 1;

        grid = new int[SIZE][SIZE];
        Random r = new Random();

        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                grid[i][j] = r.nextInt(COLOR_COUNT);

        undo.clear();
        redo.clear();
        moves = 0;
        gameOver = false;
        playerTurn = true;

        undoBtn.setEnabled(false);
        redoBtn.setEnabled(false);

        board.setPreferredSize(new Dimension(SIZE * CELL, SIZE * CELL));
        pack();
        updateMoves();
        repaint();
    }


    private void showHint() {
        if (gameOver) return;
        int best = getBestColor();
        hintLabel.setText("Hint: Try " + COLOR_NAMES[best]);
    }


    private int getBestColor() {
        ArrayList<ColorScore> list = new ArrayList<>();

        for (int c = 0; c < COLOR_COUNT; c++) {
            if (c == grid[0][0]) continue;
            int score = regionSize(simulate(c));
            list.add(new ColorScore(c, score));
        }

        int maxScore = SIZE * SIZE;
        @SuppressWarnings("unchecked")
        ArrayList<ColorScore>[] buckets = new ArrayList[maxScore + 1];
        for (int i = 0; i <= maxScore; i++) buckets[i] = new ArrayList<>();

        for (ColorScore cs : list) buckets[cs.score].add(cs);

        for (int i = maxScore; i >= 0; i--) {
            if (!buckets[i].isEmpty()) return buckets[i].get(0).color;
        }
        return grid[0][0];
    }


    void floodFill(int color) {
        int old = grid[0][0];
        if (old == color || gameOver) return;

        saveState();
        undoBtn.setEnabled(true);
        redoBtn.setEnabled(false);

        boolean[][] vis = new boolean[SIZE][SIZE];
        Queue<Point> q = new LinkedList<>();
        q.add(new Point(0, 0));
        vis[0][0] = true;

        int[][] d = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!q.isEmpty()) {
            Point p = q.poll();
            grid[p.x][p.y] = color;

            for (int[] dir : d) {
                int nx = p.x + dir[0];
                int ny = p.y + dir[1];
                if (nx>=0 && ny>=0 && nx<SIZE && ny<SIZE &&
                        !vis[nx][ny] && grid[nx][ny]==old) {
                    vis[nx][ny] = true;
                    q.add(new Point(nx, ny));
                }
            }
        }
    }

    

    int[][] simulate(int c) {
        int[][] copy = copyGrid();
        int old = copy[0][0];

        Queue<Point> q = new LinkedList<>();
        boolean[][] vis = new boolean[SIZE][SIZE];

        q.add(new Point(0,0));
        vis[0][0] = true;

        int[][] d = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!q.isEmpty()) {
            Point p = q.poll();
            copy[p.x][p.y] = c;

            for (int[] dir : d) {
                int nx = p.x + dir[0];
                int ny = p.y + dir[1];
                if (nx>=0 && ny>=0 && nx<SIZE && ny<SIZE &&
                        !vis[nx][ny] && copy[nx][ny]==old) {
                    vis[nx][ny] = true;
                    q.add(new Point(nx, ny));
                }
            }
        }
        return copy;
    }

    int regionSize(int[][] g) {
        boolean[][] vis = new boolean[SIZE][SIZE];
        Queue<Point> q = new LinkedList<>();

        q.add(new Point(0,0));
        vis[0][0] = true;
        int cnt = 0;
        int col = g[0][0];

        int[][] d = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!q.isEmpty()) {
            Point p = q.poll();
            cnt++;
            for (int[] dir : d) {
                int nx = p.x + dir[0];
                int ny = p.y + dir[1];
                if (nx>=0 && ny>=0 && nx<SIZE && ny<SIZE &&
                        !vis[nx][ny] && g[nx][ny]==col) {
                    vis[nx][ny] = true;
                    q.add(new Point(nx, ny));
                }
            }
        }
        return cnt;
    }

    void botMove() {
        int color = getBestColor();
        floodFill(color);
        moves++;
        playerTurn = true;
        checkEnd();
        repaint();
    }

    void saveState() {
        undo.push(copyGrid());
        redo.clear();
    }

    void undoMove() {
        if (!undo.empty() && !gameOver) {
            redo.push(copyGrid());
            grid = undo.pop();
            moves--;
            redoBtn.setEnabled(true);
            undoBtn.setEnabled(!undo.empty());
            updateMoves();
            repaint();
        }
    }

    void redoMove() {
        if (!redo.empty() && !gameOver) {
            undo.push(copyGrid());
            grid = redo.pop();
            moves++;
            undoBtn.setEnabled(true);
            redoBtn.setEnabled(!redo.empty());
            updateMoves();
            repaint();
        }
    }

    int[][] copyGrid() {
        int[][] c = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            c[i] = Arrays.copyOf(grid[i], SIZE);
        return c;
    }

    

    void checkEnd() {
        updateMoves();
        if (isSolved()) {
            gameOver = true;
            JOptionPane.showMessageDialog(this,
                    " You Won in " + moves + " moves!");
        }
    }

    boolean isSolved() {
        int c = grid[0][0];
        for (int[] r : grid)
            for (int x : r)
                if (x != c) return false;
        return true;
    }

    void updateMoves() {
        movesLabel.setText("Moves: " + moves + "        Team: " + TEAM_NAME);
    }

    class Board extends JPanel {
        Board() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (gameOver || !playerTurn) return;

                    int x = e.getY() / CELL;
                    int y = e.getX() / CELL;
                    if (x<0||y<0||x>=SIZE||y>=SIZE) return;

                    floodFill(grid[x][y]);
                    moves++;
                    playerTurn = !vsBot;
                    checkEnd();
                    repaint();

                    if (vsBot && !gameOver) botMove();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int i=0;i<SIZE;i++)
                for (int j=0;j<SIZE;j++) {
                    g.setColor(ALL_COLORS[grid[i][j]]);
                    g.fillRect(j*CELL, i*CELL, CELL, CELL);
                    g.setColor(Color.BLACK);
                    g.drawRect(j*CELL, i*CELL, CELL, CELL);
                }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FloodItSimonStyle::new);
    }
}