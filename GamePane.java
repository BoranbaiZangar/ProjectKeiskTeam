import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Scene;

public class GamePane extends Pane {

    private Rectangle player;

    public GamePane() {
        setStyle("-fx-background-color: black;");
        initPlayer();
    }

    private void initPlayer() {
        player = new Rectangle(Main.TILE_SIZE, Main.TILE_SIZE, Color.CYAN);
        player.setTranslateX(0);
        player.setTranslateY(0);
        getChildren().add(player);
    }

    public void startGame(Scene scene) {
        scene.setOnKeyPressed(event -> {
            double x = player.getTranslateX();
            double y = player.getTranslateY();

            if (event.getCode() == KeyCode.RIGHT && x < (Main.WIDTH - 1) * Main.TILE_SIZE) {
                player.setTranslateX(x + Main.TILE_SIZE);
            } else if (event.getCode() == KeyCode.LEFT && x > 0) {
                player.setTranslateX(x - Main.TILE_SIZE);
            } else if (event.getCode() == KeyCode.UP && y > 0) {
                player.setTranslateY(y - Main.TILE_SIZE);
            } else if (event.getCode() == KeyCode.DOWN && y < (Main.HEIGHT - 1) * Main.TILE_SIZE) {
                player.setTranslateY(y + Main.TILE_SIZE);
            }
        });
    }
}
