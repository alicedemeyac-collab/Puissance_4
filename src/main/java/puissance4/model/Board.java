package puissance4.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Grille de 6 lignes x 7 colonnes.
 * La ligne 0 est en haut, la ligne 5 en bas.
 */
public final class Board {

    public static final int ROWS = 6;
    public static final int COLS = 7;
    public static final int WIN_LENGTH = 4;

    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

    private final Player[][] cells = new Player[ROWS][COLS];
    private final int[] heights = new int[COLS];
    private int moves;

    public Board() {
    }

    private Board(Board other) {
        for (int r = 0; r < ROWS; r++) {
            System.arraycopy(other.cells[r], 0, cells[r], 0, COLS);
        }
        System.arraycopy(other.heights, 0, heights, 0, COLS);
        this.moves = other.moves;
    }

    public Board copy() {
        return new Board(this);
    }

    public Player get(int row, int col) {
        return cells[row][col];
    }

    public boolean canPlay(int col) {
        return col >= 0 && col < COLS && heights[col] < ROWS;
    }

    public boolean isFull() {
        return moves == ROWS * COLS;
    }

    /** Fait tomber un jeton dans la colonne et retourne la ligne où il atterrit. */
    public int drop(int col, Player player) {
        if (!canPlay(col)) {
            throw new IllegalArgumentException("Colonne impossible : " + col);
        }
        int row = ROWS - 1 - heights[col];
        cells[row][col] = player;
        heights[col]++;
        moves++;
        return row;
    }

    /** Annule le dernier jeton posé dans la colonne (utilisé par l'IA). */
    public void undo(int col) {
        heights[col]--;
        cells[ROWS - 1 - heights[col]][col] = null;
        moves--;
    }

    public void clear() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c] = null;
            }
        }
        for (int c = 0; c < COLS; c++) {
            heights[c] = 0;
        }
        moves = 0;
    }

    /**
     * Cherche un alignement d'au moins 4 jetons passant par la case donnée.
     *
     * @return les cases de l'alignement, ou une liste vide s'il n'y en a pas
     */
    public List<int[]> findWinningLine(int row, int col) {
        Player player = cells[row][col];
        if (player == null) {
            return List.of();
        }
        for (int[] d : DIRECTIONS) {
            List<int[]> line = new ArrayList<>();
            line.add(new int[]{row, col});
            for (int sign = -1; sign <= 1; sign += 2) {
                int r = row + sign * d[0];
                int c = col + sign * d[1];
                while (r >= 0 && r < ROWS && c >= 0 && c < COLS && cells[r][c] == player) {
                    line.add(new int[]{r, c});
                    r += sign * d[0];
                    c += sign * d[1];
                }
            }
            if (line.size() >= WIN_LENGTH) {
                return line;
            }
        }
        return List.of();
    }
}
