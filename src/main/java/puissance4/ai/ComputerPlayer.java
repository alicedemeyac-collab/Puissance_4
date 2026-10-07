package puissance4.ai;

import puissance4.model.Board;
import puissance4.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Joueur ordinateur : algorithme Negamax (variante de Minimax)
 * avec élagage alpha-bêta et une heuristique d'évaluation simple.
 */
public class ComputerPlayer {

    private static final int INF = 1_000_000;
    private static final int WIN_SCORE = 100_000;
    /** On explore d'abord les colonnes centrales : meilleur élagage. */
    private static final int[] COLUMN_ORDER = {3, 2, 4, 1, 5, 0, 6};
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

    private final Player me;
    private final Difficulty difficulty;
    private final Random random = new Random();

    public ComputerPlayer(Player me, Difficulty difficulty) {
        this.me = me;
        this.difficulty = difficulty;
    }

    /** Choisit une colonne à jouer. Ne modifie pas la grille reçue. */
    public int chooseMove(Board board) {
        Board work = board.copy();

        List<Integer> playable = new ArrayList<>();
        for (int col : COLUMN_ORDER) {
            if (work.canPlay(col)) {
                playable.add(col);
            }
        }
        if (random.nextDouble() < difficulty.getRandomMoveChance()) {
            return playable.get(random.nextInt(playable.size()));
        }

        int depth = difficulty.getDepth();
        int bestScore = Integer.MIN_VALUE;
        List<Integer> bestMoves = new ArrayList<>();

        for (int col : playable) {
            int row = work.drop(col, me);
            int score;
            if (!work.findWinningLine(row, col).isEmpty()) {
                score = WIN_SCORE + depth;
            } else {
                score = -negamax(work, depth - 1, -INF, INF, me.opponent());
            }
            work.undo(col);

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(col);
            } else if (score == bestScore) {
                bestMoves.add(col);
            }
        }
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    /** Score de la position du point de vue de {@code player}, qui doit jouer. */
    private int negamax(Board board, int depth, int alpha, int beta, Player player) {
        if (board.isFull()) {
            return 0;
        }
        if (depth == 0) {
            return evaluate(board, player);
        }
        int best = -INF;
        for (int col : COLUMN_ORDER) {
            if (!board.canPlay(col)) {
                continue;
            }
            int row = board.drop(col, player);
            int score;
            if (!board.findWinningLine(row, col).isEmpty()) {
                score = WIN_SCORE + depth; // une victoire rapide vaut plus
            } else {
                score = -negamax(board, depth - 1, -beta, -alpha, player.opponent());
            }
            board.undo(col);

            best = Math.max(best, score);
            alpha = Math.max(alpha, best);
            if (alpha >= beta) {
                break;
            }
        }
        return best;
    }

    /** Heuristique : favorise la colonne centrale et les alignements de 2-3 jetons. */
    private int evaluate(Board board, Player player) {
        Player opp = player.opponent();
        int score = 0;

        int center = Board.COLS / 2;
        for (int r = 0; r < Board.ROWS; r++) {
            Player cell = board.get(r, center);
            if (cell == player) score += 3;
            else if (cell == opp) score -= 3;
        }

        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                for (int[] d : DIRECTIONS) {
                    int endR = r + 3 * d[0];
                    int endC = c + 3 * d[1];
                    if (endR < 0 || endR >= Board.ROWS || endC < 0 || endC >= Board.COLS) {
                        continue;
                    }
                    int mine = 0;
                    int theirs = 0;
                    for (int k = 0; k < 4; k++) {
                        Player cell = board.get(r + k * d[0], c + k * d[1]);
                        if (cell == player) mine++;
                        else if (cell == opp) theirs++;
                    }
                    score += scoreWindow(mine, theirs);
                }
            }
        }
        return score;
    }

    private int scoreWindow(int mine, int theirs) {
        if (mine > 0 && theirs > 0) {
            return 0; // fenêtre bloquée pour les deux
        }
        int gain = switch (mine) {
            case 3 -> 5;
            case 2 -> 2;
            default -> 0;
        };
        int loss = switch (theirs) {
            case 3 -> 4;
            case 2 -> 1;
            default -> 0;
        };
        return gain - loss;
    }
}
