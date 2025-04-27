import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int TILE_SIZE = 40;
    public static final int WIDTH = 20;  // 20 тайлов по ширине
    public static final int HEIGHT = 15; // 15 тайлов по высоте

    @Override
    public void start(Stage stage) {
        GamePane gamePane = new GamePane();
        Scene scene = new Scene(gamePane, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        stage.setTitle("Space Escape");
        stage.setScene(scene);
        stage.show();

        gamePane.startGame(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}
