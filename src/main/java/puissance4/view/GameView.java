package puissance4.view;

import puissance4.ai.Difficulty;
import puissance4.model.GameMode;
import puissance4.model.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/** Fenêtre principale : titre, statut, scores, plateau et commandes. */
public class GameView extends BorderPane {

    private final BoardView boardView = new BoardView();
    private final Label statusLabel = new Label();
    private final Circle statusDot = new Circle(9);
    private final Label redScore = new Label();
    private final Label yellowScore = new Label();
    private final ComboBox<GameMode> modeBox = new ComboBox<>();
    private final ComboBox<Difficulty> difficultyBox = new ComboBox<>();
    private final Button newRoundButton = new Button("Nouvelle manche");
    private final Button resetButton = new Button("Réinitialiser les scores");

    public GameView() {
        getStyleClass().add("game-root");
        setPadding(new Insets(20));

        // --- haut : titre, statut, scores ---
        Label title = new Label("Puissance 4");
        title.getStyleClass().add("title");

        statusLabel.getStyleClass().add("status");
        HBox statusBox = new HBox(10, statusDot, statusLabel);
        statusBox.setAlignment(Pos.CENTER);

        redScore.setGraphic(new Circle(8, Theme.colorOf(Player.RED)));
        yellowScore.setGraphic(new Circle(8, Theme.colorOf(Player.YELLOW)));
        redScore.getStyleClass().add("score");
        yellowScore.getStyleClass().add("score");
        HBox scores = new HBox(30, redScore, yellowScore);
        scores.setAlignment(Pos.CENTER);

        VBox top = new VBox(8, title, statusBox, scores);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(0, 0, 15, 0));
        setTop(top);

        // --- centre : plateau ---
        BorderPane.setAlignment(boardView, Pos.CENTER);
        setCenter(boardView);

        // --- bas : commandes ---
        modeBox.getItems().addAll(GameMode.values());
        modeBox.setValue(GameMode.VS_COMPUTER);
        difficultyBox.getItems().addAll(Difficulty.values());
        difficultyBox.setValue(Difficulty.MEDIUM);

        Label modeLabel = new Label("Mode :");
        Label levelLabel = new Label("Niveau :");
        modeLabel.getStyleClass().add("option-label");
        levelLabel.getStyleClass().add("option-label");

        HBox options = new HBox(10, modeLabel, modeBox, levelLabel, difficultyBox);
        options.setAlignment(Pos.CENTER);
        HBox actions = new HBox(10, newRoundButton, resetButton);
        actions.setAlignment(Pos.CENTER);

        VBox bottom = new VBox(12, options, actions);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(15, 0, 0, 0));
        setBottom(bottom);
    }

    /** Met à jour le message de statut ; {@code player} peut être null (pas de pastille). */
    public void setStatus(String text, Player player) {
        statusLabel.setText(text);
        statusDot.setVisible(player != null);
        statusDot.setManaged(player != null);
        if (player != null) {
            statusDot.setFill(Theme.colorOf(player));
        }
    }

    public void setScores(int red, int yellow) {
        redScore.setText(Player.RED.getLabel() + " : " + red);
        yellowScore.setText(Player.YELLOW.getLabel() + " : " + yellow);
    }

    public BoardView getBoardView() { return boardView; }
    public ComboBox<GameMode> getModeBox() { return modeBox; }
    public ComboBox<Difficulty> getDifficultyBox() { return difficultyBox; }
    public Button getNewRoundButton() { return newRoundButton; }
    public Button getResetButton() { return resetButton; }
}
