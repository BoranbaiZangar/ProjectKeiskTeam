package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;
import javafx.scene.input.KeyCode;

public class Main extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final Set<KeyCode> keysPressed = new HashSet<>();
    private Player player;
    private Level level;
    private Image backgroundImage;
    private MediaPlayer backgroundMusic;
    private Portal portal;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Space Escape");

        Group root = new Group();
        Scene scene = new Scene(root);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        scene.setOnKeyPressed(event -> keysPressed.add(event.getCode()));
        scene.setOnKeyReleased(event -> keysPressed.remove(event.getCode()));

        backgroundImage = new Image(getClass().getResourceAsStream("/images/background.jpg"));
        Media media = new Media(getClass().getResource("/sounds/background-music.mp3").toString());
        backgroundMusic = new MediaPlayer(media);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.play();

        level = new Level("level1.txt");
        player = new Player(100, 500, HEIGHT - 50, level.getLines());
        portal = new Portal(700, 500); // Примерная позиция портала на карте


        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render(gc);
            }
        }.start();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void update() {
        player.update(keysPressed);

        if (portal.checkCollision(player)) {
            System.out.println("Переход на следующий уровень!");
            // Здесь можем переходить на следующий уровень
        }
    }


    private void render(GraphicsContext gc) {
        gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);
        level.render(gc);
        portal.render(gc);
        player.render(gc);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
