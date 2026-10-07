# 🔴🟡 Puissance 4 — Java & JavaFX

Un jeu de **Puissance 4** (Connect Four) développé en **Java 17** avec **JavaFX**, jouable à deux ou seul contre un ordinateur dont l'intelligence artificielle repose sur l'algorithme **Minimax / Negamax avec élagage alpha-bêta**.

<!-- Ajoutez une capture d'écran : placez-la dans docs/screenshot.png -->
<!-- ![Capture d'écran](docs/screenshot.png) -->

## ✨ Fonctionnalités

- Plateau classique de 6 lignes × 7 colonnes
- Deux modes de jeu : **2 joueurs** (même écran) ou **contre l'ordinateur**
- 3 niveaux de difficulté : Facile, Moyen, Difficile
- Animation de chute des jetons et aperçu du jeton au survol d'une colonne
- Détection des victoires (horizontale, verticale, diagonales) avec **clignotement des jetons gagnants**
- Détection du match nul
- Scores conservés sur plusieurs manches, le joueur qui commence alterne à chaque manche
- Calcul de l'IA dans un thread séparé : l'interface ne se fige jamais

## 🧰 Prérequis

- **JDK 17** ou supérieur
- **Maven 3.6** ou supérieur

JavaFX est téléchargé automatiquement par Maven, il n'y a rien d'autre à installer.

## 🚀 Lancer le jeu

```bash
git clone https://github.com/<votre-pseudo>/puissance4.git
cd puissance4
mvn javafx:run
```

Pour compiler uniquement :

```bash
mvn clean compile
```

### Depuis un IDE (IntelliJ IDEA, Eclipse, VS Code…)

1. Importez le dossier comme **projet Maven existant**.
2. Lancez la classe `com.example.puissance4.Main`, ou exécutez `javafx:run` depuis le panneau Maven.

## 🎮 Comment jouer

1. Choisissez le **mode** et, contre l'ordinateur, le **niveau**.
2. Survolez une colonne : un aperçu de votre jeton apparaît.
3. **Cliquez** sur la colonne pour y lâcher votre jeton.
4. Le premier joueur qui aligne **4 jetons** (horizontalement, verticalement ou en diagonale) remporte la manche.
5. Cliquez sur **Nouvelle manche** pour rejouer (les scores sont conservés) ou sur **Réinitialiser les scores** pour tout remettre à zéro.

> Changer de mode remet la partie et les scores à zéro. Contre l'ordinateur, vous jouez les jetons rouges et l'ordinateur les jaunes.

## 🗂️ Structure du projet

```
puissance4/
├── pom.xml
├── README.md
├── LICENSE
├── .gitignore
└── src/main/
    ├── java/com/example/puissance4/
    │   ├── Main.java                  # Point d'entrée JavaFX
    │   ├── model/                     # Logique du jeu (aucune dépendance JavaFX)
    │   │   ├── Player.java            # Joueurs ROUGE / JAUNE
    │   │   ├── GameMode.java          # 2 joueurs / contre l'ordinateur
    │   │   ├── Board.java             # Grille, pose de jetons, détection d'alignements
    │   │   └── Game.java              # Règles, tour de jeu, état de la manche, scores
    │   ├── ai/                        # Intelligence artificielle
    │   │   ├── Difficulty.java        # Niveaux (profondeur de recherche)
    │   │   └── ComputerPlayer.java    # Negamax + élagage alpha-bêta
    │   ├── view/                      # Interface JavaFX
    │   │   ├── GameView.java          # Fenêtre principale (statut, scores, commandes)
    │   │   ├── BoardView.java         # Plateau, jetons et animations
    │   │   └── Theme.java             # Couleurs
    │   └── controller/
    │       └── GameController.java    # Relie modèle, IA et vue
    └── resources/
        └── style.css                  # Feuille de style JavaFX
```

Le projet suit une séparation **Modèle / Vue / Contrôleur** : le package `model` ne connaît ni JavaFX ni l'IA, ce qui le rend facile à tester et à réutiliser.

## 🤖 Comment fonctionne l'IA

L'ordinateur utilise **Negamax**, une version simplifiée de Minimax, avec **élagage alpha-bêta** :

1. Il simule tous les coups possibles sur plusieurs tours d'avance (la **profondeur** dépend du niveau).
2. Il suppose que l'adversaire joue toujours le meilleur coup possible.
3. À la profondeur maximale, une **heuristique** évalue la position : bonus pour la colonne centrale, pour les alignements de 2 ou 3 jetons non bloqués, malus pour ceux de l'adversaire.
4. Une victoire plus rapide vaut plus de points qu'une victoire tardive.
5. Les colonnes sont explorées en commençant par le centre, ce qui améliore l'efficacité de l'élagage.

| Niveau    | Profondeur | Particularité                       |
|-----------|:----------:|-------------------------------------|
| Facile    | 2          | Joue un coup au hasard 1 fois sur 4 |
| Moyen     | 4          | —                                   |
| Difficile | 6          | —                                   |

## 🔧 Pistes d'amélioration

- Choisir la couleur ou qui commence
- Bouton « Annuler le dernier coup »
- Sons et effets visuels supplémentaires
- Fenêtre redimensionnable
- Mode en réseau
- Tests unitaires JUnit sur `Board` et `Game`

## 📄 Licence

Ce projet est distribué sous licence **MIT** — voir le fichier [LICENSE](LICENSE).

## 👤 Auteur

Votre Nom — [@votre-pseudo](https://github.com/votre-pseudo)
