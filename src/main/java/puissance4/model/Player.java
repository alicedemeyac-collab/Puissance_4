package puissance4.model;

/** Les deux joueurs de la partie. Le modèle ne dépend pas de JavaFX. */
public enum Player {
    RED("Rouge"),
    YELLOW("Jaune");

    private final String label;

    Player(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Player opponent() {
        return this == RED ? YELLOW : RED;
    }
}
