package puissance4.controller;

import puissance4.ai.ComputerPlayer;
import puissance4.model.Game;
import puissance4.model.GameMode;
import puissance4.model.Player;
import puissance4.view.BoardView;
import puissance4.view.GameView;
import javafx.concurrent.Task;

/**
 * Fait le lien entre le modèle ({@link Game}), l'IA et la vue ({@link GameView}).
 * L'ordinateur calcule son coup dans un thread séparé pour ne pas figer l'interface.
 */
public class GameController {

    /** L'ordinateur joue toujours les jetons jaunes. */
    private static final Player AI_PLAYER = Player.YELLOW;

    private final Game game = new Game();
    private final GameView view;
    private final BoardView boardView;

    private ComputerPlayer ai;
    /** Vrai pendant une animation ou un calcul de l'IA. */
    private boolean busy;
    /** Incrémenté à chaque remise à zéro pour ignorer les callbacks périmés. */
    private int token;

    public GameController(GameView view) {
        this.view = view;
        this.boardView = view.getBoardView();

        boardView.setOnColumnClicked(this::onColumnClicked);
        view.getNewRoundButton().setOnAction(e -> newRound());
        view.getResetButton().setOnAction(e -> resetAll());
        view.getModeBox().setOnAction(e -> onModeChanged());
        view.getDifficultyBox().setOnAction(e -> createAi());

        onModeChanged();
    }

    // ---------- Actions de l'interface ----------

    private void onColumnClicked(int col) {
        if (busy || isAiTurn() || game.getStatus() != Game.Status.IN_PROGRESS) {
            return;
        }
        playMove(col);
    }

    private void onModeChanged() {
        boolean vsComputer = view.getModeBox().getValue() == GameMode.VS_COMPUTER;
        view.getDifficultyBox().setDisable(!vsComputer);
        createAi();
        resetAll();
    }

    private void createAi() {
        boolean vsComputer = view.getModeBox().getValue() == GameMode.VS_COMPUTER;
        ai = vsComputer ? new ComputerPlayer(AI_PLAYER, view.getDifficultyBox().getValue()) : null;
    }

    private void newRound() {
        resetBoardView();
        game.newRound();
        afterStateChange();
    }

    private void resetAll() {
        resetBoardView();
        game.resetAll();
        afterStateChange();
    }

    private void resetBoardView() {
        token++;
        busy = false;
        boardView.clear();
    }

    // ---------- Déroulement d'un coup ----------

    private void playMove(int col) {
        Player mover = game.getCurrentPlayer();
        int row = game.play(col);
        if (row < 0) {
            return;
        }
        busy = true;
        boardView.setInteractive(false);
        int myToken = token;
        boardView.animateDrop(row, col, mover, () -> {
            if (myToken != token) {
                return; // la manche a été réinitialisée entre-temps
            }
            busy = false;
            if (game.getStatus() == Game.Status.WON) {
                boardView.highlightWinner(game.getWinningCells());
            }
            afterStateChange();
        });
    }

    private void afterStateChange() {
        boardView.setCurrentPlayer(game.getCurrentPlayer());
        refreshUi();
        if (isAiTurn() && !busy) {
            startAiTurn();
        }
    }

    private void startAiTurn() {
        busy = true;
        int myToken = token;
        var snapshot = game.getBoard().copy();

        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                Thread.sleep(250); // petite pause pour que le coup soit lisible
                return ai.chooseMove(snapshot);
            }
        };
        task.setOnSucceeded(e -> {
            if (myToken != token) {
                return;
            }
            busy = false;
            playMove(task.getValue());
        });
        task.setOnFailed(e -> busy = false);

        Thread thread = new Thread(task, "ai-thread");
        thread.setDaemon(true);
        thread.start();
    }

    private boolean isAiTurn() {
        return ai != null
                && game.getStatus() == Game.Status.IN_PROGRESS
                && game.getCurrentPlayer() == AI_PLAYER;
    }

    // ---------- Mise à jour de l'affichage ----------

    private void refreshUi() {
        view.setScores(game.getScore(Player.RED), game.getScore(Player.YELLOW));

        switch (game.getStatus()) {
            case WON -> {
                Player winner = game.getWinner();
                String text;
                if (ai == null) {
                    text = winner.getLabel() + " a gagné !";
                } else {
                    text = winner == AI_PLAYER ? "L'ordinateur a gagné !" : "Vous avez gagné !";
                }
                view.setStatus(text, winner);
            }
            case DRAW -> view.setStatus("Match nul !", null);
            case IN_PROGRESS -> {
                Player current = game.getCurrentPlayer();
                if (isAiTurn()) {
                    view.setStatus("L'ordinateur réfléchit…", current);
                } else if (ai != null) {
                    view.setStatus("À vous de jouer", current);
                } else {
                    view.setStatus("Tour de " + current.getLabel(), current);
                }
            }
        }
        boardView.setInteractive(
                game.getStatus() == Game.Status.IN_PROGRESS && !isAiTurn() && !busy);
    }
}
