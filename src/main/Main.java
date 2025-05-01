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
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final Set<KeyCode> keysPressed = new HashSet<>();
    private Player player;
    private Level level;
    private Portal portal;

    private Image backgroundImage;
    private MediaPlayer backgroundMusic;

    private final List<String> levelNames = List.of("level1.txt", "level2.txt", "level3.txt");
    private int levelIndex = 0;

    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Space Escape");
        primaryStage.setResizable(false);

        Group root = new Group();
        Scene scene = new Scene(root);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        gc = canvas.getGraphicsContext2D();

        scene.setOnKeyPressed(event -> keysPressed.add(event.getCode()));
        scene.setOnKeyReleased(event -> keysPressed.remove(event.getCode()));

        // Загрузка фона
        backgroundImage = new Image(getClass().getResourceAsStream("/images/background.jpg"));

        // Музыка
        Media media = new Media(getClass().getResource("/sounds/background-music.mp3").toString());
        backgroundMusic = new MediaPlayer(media);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.play();

        // Загружаем первый уровень
        loadLevel(levelIndex);

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        gameLoop.start();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadLevel(int index) {
        level = new Level(levelNames.get(index));
        player = new Player(level.getStartX(), level.getStartY(), HEIGHT - 50, level.getTiles());
        portal = new Portal(level.getPortalX(), level.getPortalY());
    }

    private void update() {
        player.update(keysPressed);

        if (portal.checkCollision(player)) {
            levelIndex++;
            if (levelIndex < levelNames.size()) {
                loadLevel(levelIndex);
            } else {
                backgroundMusic.stop();
                showWinScreen();
                stopGameLoop();
            }
        }
    }

    private void render() {
        gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);
        level.render(gc);
        portal.render(gc);
        player.render(gc);
    }

    private void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    private void showWinScreen() {
        Stage winStage = new Stage();
        winStage.setTitle("Победа!");

        Group root = new Group();
        Scene scene = new Scene(root, 400, 250, Color.BLACK);

        Text winText = new Text("🎉 ПОБЕДА! Все уровни пройдены!");
        winText.setFill(Color.WHITE);
        winText.setStyle("-fx-font-size: 20px;");
        winText.setX(40);
        winText.setY(100);

        // Кнопка "Начать заново"
        javafx.scene.control.Button restartButton = new javafx.scene.control.Button("🔁 Начать заново");
        restartButton.setLayoutX(80);
        restartButton.setLayoutY(150);
        restartButton.setOnAction(e -> {
            winStage.close();
            levelIndex = 0;
            backgroundMusic.play();
            loadLevel(levelIndex);
            gameLoop.start();
        });

        // Кнопка "Выйти из игры"
        javafx.scene.control.Button exitButton = new javafx.scene.control.Button("🚪 Выйти");
        exitButton.setLayoutX(230);
        exitButton.setLayoutY(150);
        exitButton.setOnAction(e -> {
            System.exit(0); // Полное завершение игры
        });

        root.getChildren().addAll(winText, restartButton, exitButton);
        winStage.setScene(scene);
        winStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
