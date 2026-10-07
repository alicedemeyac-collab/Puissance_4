package puissance4.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Règles et état d'une partie (manches successives avec scores). */
public class Game {

    public enum Status { IN_PROGRESS, WON, DRAW }

    private final Board board = new Board();
    private final Map<Player, Integer> scores = new EnumMap<>(Player.class);

    private Player startingPlayer = Player.RED;
    private Player currentPlayer = Player.RED;
    private Status status = Status.IN_PROGRESS;
    private Player winner;
    private List<int[]> winningCells = List.of();

    public Game() {
        resetAll();
    }

    /**
     * Joue un jeton pour le joueur courant.
     *
     * @return la ligne où le jeton atterrit, ou -1 si le coup est invalide
     */
    public int play(int col) {
        if (status != Status.IN_PROGRESS || !board.canPlay(col)) {
            return -1;
        }
        int row = board.drop(col, currentPlayer);
        List<int[]> line = board.findWinningLine(row, col);
        if (!line.isEmpty()) {
            status = Status.WON;
            winner = currentPlayer;
            winningCells = line;
            scores.merge(winner, 1, Integer::sum);
        } else if (board.isFull()) {
            status = Status.DRAW;
        } else {
            currentPlayer = currentPlayer.opponent();
        }
        return row;
    }

    /** Nouvelle manche : le joueur qui commence alterne. */
    public void newRound() {
        startingPlayer = startingPlayer.opponent();
        startRound();
    }

    /** Remet les scores à zéro et recommence avec le joueur Rouge. */
    public void resetAll() {
        scores.put(Player.RED, 0);
        scores.put(Player.YELLOW, 0);
        startingPlayer = Player.RED;
        startRound();
    }

    private void startRound() {
        board.clear();
        currentPlayer = startingPlayer;
        status = Status.IN_PROGRESS;
        winner = null;
        winningCells = List.of();
    }

    public Board getBoard() { return board; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public Status getStatus() { return status; }
    public Player getWinner() { return winner; }
    public List<int[]> getWinningCells() { return winningCells; }
    public int getScore(Player player) { return scores.get(player); }
}
