package puissance4.view;

import puissance4.model.Board;
import puissance4.model.Player;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Affichage de la grille : plateau bleu percé, jetons animés,
 * aperçu du jeton au-dessus de la colonne survolée.
 * La rangée du haut (hors plateau) sert de zone de lâcher.
 */
public class BoardView extends Pane {

    static final double CELL = 90;
    private static final double RADIUS = 38;
    private static final int ROWS = Board.ROWS;
    private static final int COLS = Board.COLS;

    private final Pane discLayer = new Pane();
    private final Circle preview = new Circle(RADIUS);
    private final Circle[][] discs = new Circle[ROWS][COLS];
    private final List<Animation> winAnimations = new ArrayList<>();

    private IntConsumer onColumnClicked = col -> { };
    private Player currentPlayer = Player.RED;
    private boolean interactive = true;

    public BoardView() {
        double width = COLS * CELL;
        double height = (ROWS + 1) * CELL;
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);

        getChildren().add(discLayer);
        getChildren().add(buildBoardShape());

        preview.setOpacity(0.6);
        preview.setCenterY(CELL / 2);
        preview.setVisible(false);
        preview.setMouseTransparent(true);
        preview.setFill(Theme.colorOf(currentPlayer));
        getChildren().add(preview);

        for (int c = 0; c < COLS; c++) {
            getChildren().add(buildHitbox(c));
        }
    }

    // ---------- API publique ----------

    public void setOnColumnClicked(IntConsumer handler) {
        this.onColumnClicked = handler;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
        preview.setFill(Theme.colorOf(player));
    }

    /** Active ou désactive les clics et l'aperçu du jeton. */
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
        if (!interactive) {
            preview.setVisible(false);
        }
    }

    /** Anime la chute d'un jeton jusqu'à sa case, puis appelle {@code onFinished}. */
    public void animateDrop(int row, int col, Player player, Runnable onFinished) {
        preview.setVisible(false);

        Circle disc = createDisc(player);
        disc.setCenterX(centerX(col));
        disc.setCenterY(centerY(row));
        discs[row][col] = disc;
        discLayer.getChildren().add(disc);

        double fall = centerY(row) - CELL / 2;
        TranslateTransition fallAnimation =
                new TranslateTransition(Duration.millis(200 + 70 * row), disc);
        fallAnimation.setFromY(-fall);
        fallAnimation.setToY(0);
        fallAnimation.setInterpolator(Interpolator.EASE_IN);
        fallAnimation.setOnFinished(e -> onFinished.run());
        fallAnimation.play();
    }

    /** Fait clignoter les 4 jetons (ou plus) gagnants. */
    public void highlightWinner(List<int[]> cells) {
        for (int[] cell : cells) {
            Circle disc = discs[cell[0]][cell[1]];
            if (disc == null) {
                continue;
            }
            disc.setStroke(Color.WHITE);
            disc.setStrokeWidth(5);
            FadeTransition blink = new FadeTransition(Duration.millis(450), disc);
            blink.setFromValue(1.0);
            blink.setToValue(0.35);
            blink.setAutoReverse(true);
            blink.setCycleCount(Animation.INDEFINITE);
            blink.play();
            winAnimations.add(blink);
        }
    }

    /** Vide la grille pour une nouvelle manche. */
    public void clear() {
        winAnimations.forEach(Animation::stop);
        winAnimations.clear();
        discLayer.getChildren().clear();
        for (Circle[] row : discs) {
            java.util.Arrays.fill(row, null);
        }
    }

    // ---------- Construction ----------

    private Shape buildBoardShape() {
        Shape holes = null;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Circle hole = new Circle(centerX(c), centerY(r), RADIUS);
                holes = (holes == null) ? hole : Shape.union(holes, hole);
            }
        }
        Rectangle frame = new Rectangle(0, CELL, COLS * CELL, ROWS * CELL);
        frame.setArcWidth(30);
        frame.setArcHeight(30);

        Shape board = Shape.subtract(frame, holes);
        board.setFill(Theme.BOARD);
        board.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.5)));
        board.setMouseTransparent(true);
        return board;
    }

    private Rectangle buildHitbox(int col) {
        Rectangle box = new Rectangle(col * CELL, 0, CELL, (ROWS + 1) * CELL);
        box.setFill(Color.TRANSPARENT);
        box.setCursor(Cursor.HAND);
        box.setOnMouseEntered(e -> {
            if (interactive) {
                preview.setCenterX(centerX(col));
                preview.setVisible(true);
            }
        });
        box.setOnMouseExited(e -> preview.setVisible(false));
        box.setOnMouseClicked(e -> {
            if (interactive) {
                onColumnClicked.accept(col);
            }
        });
        return box;
    }

    private Circle createDisc(Player player) {
        Color base = Theme.colorOf(player);
        Circle disc = new Circle(RADIUS);
        disc.setFill(new RadialGradient(0, 0, 0.35, 0.3, 0.9, true, CycleMethod.NO_CYCLE,
                new Stop(0, base.brighter()), new Stop(1, base.darker())));
        disc.setStroke(base.darker().darker());
        disc.setStrokeWidth(2);
        return disc;
    }

    private static double centerX(int col) {
        return col * CELL + CELL / 2;
    }

    /** Le plateau commence sous la rangée de lâcher, d'où le (row + 1). */
    private static double centerY(int row) {
        return (row + 1) * CELL + CELL / 2;
    }
}
