package puissance4;

import puissance4.controller.GameController;
import puissance4.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Point d'entrée de l'application JavaFX. */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        GameView view = new GameView();
        new GameController(view);

        Scene scene = new Scene(view);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setTitle("Puissance 4");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
