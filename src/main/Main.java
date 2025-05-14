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
import javafx.scene.control.Button;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main extends Application {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final int DEATH_DELAY = 800;

    private boolean isMusicMuted = false;  // Для фонової музыки
    private boolean isSoundMuted = false;  // Для звуков игры (например, прыжка)

    private final Set<KeyCode> keysPressed = new HashSet<>();
    private Player player;
    private Level level;
    private Portal portal;
    private boolean isGamePaused = false; // Флаг паузы
    private Image backgroundImage;
    private MediaPlayer backgroundMusic;

    private final List<String> levelNames = List.of("level1.txt", "level2.txt", "level3.txt", "level4.txt");
    private int levelIndex = 0;

    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private int lives = 3;

    private Scene menuScene;
    private Scene settingsScene;
    private Image laserImage;   // Изображение лазера
    private Image rocketImage;  // Изображение ракеты

    private Image playerImage;  // Изображение игрока
    private Image bulletImage;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Space Escape");
        primaryStage.setResizable(false);
        loadLevel(levelIndex);
        // Заранее загружаем фон и музыку
        playerImage = new Image(getClass().getResourceAsStream("/images/player.png"));
        Image playerRight = new Image(getClass().getResourceAsStream("/images/player_right.png"));
        Image playerLeft = new Image(getClass().getResourceAsStream("/images/player_left.png"));

        bulletImage = new Image(getClass().getResourceAsStream("/images/bullet.png"));
        backgroundImage = new Image(getClass().getResourceAsStream("/images/background.png"));
        Media media = new Media(getClass().getResource("/sounds/background-music.mp3").toString());
        backgroundMusic = new MediaPlayer(media);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.play();

        // Показать главное меню
        showMainMenu(primaryStage);
        primaryStage.show();
    }

    private void loadLevel(int index) {
        level = new Level(levelNames.get(index));
        player = new Player(100, 500, level.getTiles(), playerImage, bulletImage, laserImage, rocketImage);  // Передаем все 4 изображения
        portal = new Portal(level.getPortalX(), level.getPortalY());
    }

    private void update() {
        player.update(keysPressed);

        // смерть от шипов
        for (Tile tile : level.getTiles()) {
            if (tile.getType() == Tile.Type.SPIKES && tile.intersects(player)) {
                loseLife("Наступил на шипы");
                return;
            }
        }

        // смерть при падении
        if (player.getY() > HEIGHT) {
            loseLife("Упал с карты");
            return;
        }

        // переход через портал
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
        // UI: отрисовка количества жизней
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(20));
        gc.fillText("❤️ Жизни: " + lives, 10, 25);
    }

    private void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    private void loseLife(String reason) {
        System.out.println(reason + ". Осталось жизней: " + (lives - 1));
        lives--;

        stopGameLoop();

        if (lives <= 0) {
            backgroundMusic.stop();
            showLoseScreen();
        } else {
            // Задержка перед рестартом уровня
            new Thread(() -> {
                try {
                    Thread.sleep(800); // небольшая пауза
                } catch (InterruptedException ignored) {}
                javafx.application.Platform.runLater(() -> {
                    loadLevel(levelIndex);
                    gameLoop.start();
                });
            }).start();
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

        Button restartButton = new Button("🔁 Начать заново");
        restartButton.setLayoutX(80);
        restartButton.setLayoutY(150);
        restartButton.setOnAction(e -> {
            winStage.close();
            restartGame();
        });

        Button exitButton = new Button("🚪 Выйти");
        exitButton.setLayoutX(230);
        exitButton.setLayoutY(150);
        exitButton.setOnAction(e -> System.exit(0));

        Button soundButton = new Button(isSoundMuted ? "🔊 Включить звук" : "🔇 Выключить звук");
        soundButton.setLayoutX(80);
        soundButton.setLayoutY(270);
        soundButton.setOnAction(e -> {
            isSoundMuted = !isSoundMuted;
            if (isSoundMuted) {
                backgroundMusic.setMute(true);
                player.setJumpSoundMuted(true);
            } else {
                backgroundMusic.setMute(false);
                player.setJumpSoundMuted(false);
            }
            soundButton.setText(isSoundMuted ? "🔊 Включить звук" : "🔇 Выключить звук");
        });

        root.getChildren().addAll(winText, restartButton, exitButton, soundButton);
        winStage.setScene(scene);
        winStage.show();


    }

    private void showLoseScreen() {
        Stage loseStage = new Stage();
        loseStage.setTitle("Поражение!");

        Group root = new Group();
        Scene scene = new Scene(root, 400, 250, Color.BLACK);

        Text loseText = new Text("💀 Вы проиграли! Все жизни потеряны.");
        loseText.setFill(Color.RED);
        loseText.setStyle("-fx-font-size: 20px;");
        loseText.setX(30);
        loseText.setY(100);

        Button restartButton = new Button("🔁 Начать заново");
        restartButton.setLayoutX(80);
        restartButton.setLayoutY(150);
        restartButton.setOnAction(e -> {
            loseStage.close();
            restartGame();
        });

        Button exitButton = new Button("🚪 Выйти");
        exitButton.setLayoutX(230);
        exitButton.setLayoutY(150);
        exitButton.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(loseText, restartButton, exitButton);
        loseStage.setScene(scene);
        loseStage.show();
    }

    private void restartGame() {
        levelIndex = 0;
        lives = 3;
        keysPressed.clear();
        backgroundMusic.play();
        loadLevel(levelIndex);
        gameLoop.start();
    }

    private Scene createGameScene(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        gc = canvas.getGraphicsContext2D();  // Инициализация GraphicsContext

        scene.setOnKeyPressed(e -> {
            keysPressed.add(e.getCode());
            if (e.getCode() == KeyCode.E) {
                player.shoot();  // 👉 игрок стреляет
            }
        });


        scene.setOnKeyReleased(e -> keysPressed.remove(e.getCode()));

        loadLevel(levelIndex);

        // Добавляем кнопку паузы в правый верхний угол
        Button pauseButton = new Button("⏸ Пауза");
        pauseButton.setLayoutX(WIDTH - 90);  // Располагаем кнопку на 90 пикселей влево от правого края
        pauseButton.setLayoutY(10);  // Небольшой отступ от верхнего края
        pauseButton.setOnAction(e -> {
            isGamePaused = !isGamePaused;  // Переключаем состояние паузы
            pauseButton.setText(isGamePaused ? "▶ Возобновить" : "⏸ Пауза");

        });
        pauseButton.setFocusTraversable(false);

        root.getChildren().add(pauseButton);  // Добавляем кнопку паузы в игровую сцену

        // Создаем игровой цикл
        gameLoop = new AnimationTimer() {
            @Override public void handle(long now) {
                if (!isGamePaused) {  // Если игра не на паузе
                    update();
                    render();
                }
            }
        };

        return scene;
    }


    private void showMainMenu(Stage stage) {
        Group root = new Group();
        menuScene = new Scene(root, WIDTH, HEIGHT);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Фон
        if (backgroundImage == null)
            backgroundImage = new Image(getClass().getResourceAsStream("/images/background.png"));
        gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);

        // Заголовок
        Text title = new Text("🌌 SPACE ESCAPE");
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        title.setX(250);
        title.setY(150);

        // Кнопка Старт
        Button startButton = new Button("▶ Начать игру");
        startButton.setLayoutX(330);
        startButton.setLayoutY(220);
        startButton.setOnAction(e -> {
            stage.setScene(createGameScene(stage));
            gameLoop.start();
        });

        // Кнопка Выход
        Button exitButton = new Button("❌ Выйти");
        exitButton.setLayoutX(345);
        exitButton.setLayoutY(270);
        exitButton.setOnAction(e -> System.exit(0));

        // Кнопка Настроек
        Button settingsButton = new Button("⚙ Настройки");
        settingsButton.setLayoutX(330);
        settingsButton.setLayoutY(320);
        settingsButton.setOnAction(e -> showSettingsMenu(stage));

        root.getChildren().addAll(title, startButton, exitButton, settingsButton);
        stage.setScene(menuScene);


    }

    private void showSettingsMenu(Stage stage) {
        Group root = new Group();
        settingsScene = new Scene(root, WIDTH, HEIGHT);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Фон
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        Text settingsTitle = new Text("Настройки");
        settingsTitle.setFill(Color.WHITE);
        settingsTitle.setStyle("-fx-font-size: 30px;");
        settingsTitle.setX(300);
        settingsTitle.setY(100);

        // Кнопка для звука
        Button soundButton = new Button(isSoundMuted ? "🔊 Включить звук" : "🔇 Выключить звук");
        soundButton.setLayoutX(300);
        soundButton.setLayoutY(150);
        soundButton.setOnAction(e -> {
            isSoundMuted = !isSoundMuted;
            if (isSoundMuted) {
                backgroundMusic.setMute(true);
                if (player != null) {  // Проверяем, что player не равен null
                    player.setJumpSoundMuted(true);
                }
            } else {
                backgroundMusic.setMute(false);
                if (player != null) {
                    player.setJumpSoundMuted(false);
                }
            }
            soundButton.setText(isSoundMuted ? "🔊 Включить звук" : "🔇 Выключить звук");
        });

        // Кнопка для музыки
        Button musicButton = new Button(isMusicMuted ? "🎶 Включить музыку" : "🔇 Выключить музыку");
        musicButton.setLayoutX(300);
        musicButton.setLayoutY(200);
        musicButton.setOnAction(e -> {
            isMusicMuted = !isMusicMuted;
            if (isMusicMuted) {
                backgroundMusic.setMute(true);
            } else {
                backgroundMusic.setMute(false);
            }
            musicButton.setText(isMusicMuted ? "🎶 Включить музыку" : "🔇 Выключить музыку");
        });

        // Кнопка возврата в меню
        Button backButton = new Button("↩ Назад");
        backButton.setLayoutX(350);
        backButton.setLayoutY(270);
        backButton.setOnAction(e -> stage.setScene(menuScene));

        root.getChildren().addAll(settingsTitle, soundButton, musicButton, backButton);
        stage.setScene(settingsScene);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
