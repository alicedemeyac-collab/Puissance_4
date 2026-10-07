package puissance4.ai;

/** Niveaux de difficulté de l'ordinateur. */
public enum Difficulty {
    EASY("Facile", 2, 0.25),
    MEDIUM("Moyen", 4, 0.0),
    HARD("Difficile", 6, 0.0);

    private final String label;
    private final int depth;
    private final double randomMoveChance;

    Difficulty(String label, int depth, double randomMoveChance) {
        this.label = label;
        this.depth = depth;
        this.randomMoveChance = randomMoveChance;
    }

    /** Profondeur de recherche (nombre de coups anticipés). */
    public int getDepth() { return depth; }

    /** Probabilité de jouer un coup au hasard (pour rendre l'IA battable). */
    public double getRandomMoveChance() { return randomMoveChance; }

    @Override
    public String toString() {
        return label;
    }
}
