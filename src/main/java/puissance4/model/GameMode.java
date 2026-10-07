package puissance4.model;

/** Mode de jeu : deux humains ou un humain contre l'ordinateur. */
public enum GameMode {
    TWO_PLAYERS("2 joueurs"),
    VS_COMPUTER("Contre l'ordinateur");

    private final String label;

    GameMode(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
