package puissance4.view;

import puissance4.model.Player;
import javafx.scene.paint.Color;

/** Couleurs partagées par les vues. */
final class Theme {

    static final Color BOARD = Color.web("#1e4fd8");
    private static final Color RED = Color.web("#e53935");
    private static final Color YELLOW = Color.web("#fdd835");

    private Theme() {
    }

    static Color colorOf(Player player) {
        return player == Player.RED ? RED : YELLOW;
    }
}
