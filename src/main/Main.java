package main;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main extends Application {

    // Константы для размеров окна
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final int DEATH_DELAY_SECONDS = 2; // Задержка в секундах перед возрождением
    private static final int FADE_DURATION_MS = 500; // Длительность анимации затемнения

    // Пути к ресурсам
    private static final String BACKGROUND_IMAGE_PATH = "/images/background.png";
    private static final String PLAYER_IMAGE_PATH = "/images/player.png";
    private static final String PLAYER_RIGHT_IMAGE_PATH = "/images/player_right.png";
    private static final String PLAYER_LEFT_IMAGE_PATH = "/images/player_left.png";
    private static final String ROCKET_IMAGE_PATH = "/images/rocket.png";
    private static final String LASER_IMAGE_PATH = "/images/laser.png";
    private static final String BULLET_IMAGE_PATH = "/images/bullet.png";
    private static final String BACKGROUND_MUSIC_PATH = "/sounds/background-music.mp3";

    // Файл для сохранения прогресса
    private static final String PROGRESS_FILE = "progress.dat";

    // Состояние игры
    private boolean isMusicMuted = false;
    private boolean isSoundMuted = false;
    private boolean isGamePaused = false;
    private int lives = 3;
    private int levelIndex = 0;
    private int highScore = 0; // Рекорд очков

    // Объекты игры
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private Player player;
    private Level level;
    private Portal portal;
    private Inventory playerInventory;
    private EnemyManager enemyManager;
    private ProjectileManager projectileManager;

    // Ресурсы
    private Image backgroundImage;
    private MediaPlayer backgroundMusic;
    private Image playerImage;
    private Image playerRight;
    private Image playerLeft;
    private Image rocketImage;
    private Image laserImage;
    private Image bulletImage;

    // JavaFX компоненты
    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    private Scene menuScene;
    private Scene settingsScene;
    private Scene inventoryScene;
    private Scene gameScene;
    private Group gameRoot;
    private Stage primaryStage;
    private final List<String> levelNames = List.of("level1.txt", "level2.txt", "level3.txt", "level4.txt");

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Space Escape");
        primaryStage.setResizable(false);

        // Загрузка ресурсов
        loadResources();

        // Инициализация музыки
        try {
            Media media = new Media(getClass().getResource(BACKGROUND_MUSIC_PATH).toString());
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            if (!isMusicMuted) backgroundMusic.play();
        } catch (Exception e) {
            System.err.println("Ошибка загрузки музыки: " + e.getMessage());
            e.printStackTrace();
        }

        // Инициализация объектов
        projectileManager = new ProjectileManager(this);
        playerInventory = new Inventory();

        // Загрузка сохранённого прогресса
        loadProgress();

        // Показываем главное меню
        showMainMenu(primaryStage);
        primaryStage.show();

        // Сохранение прогресса при закрытии приложения
        primaryStage.setOnCloseRequest(e -> saveProgress());
    }

    private void loadResources() {
        try {
            backgroundImage = new Image(getClass().getResourceAsStream(BACKGROUND_IMAGE_PATH));
            playerImage = new Image(getClass().getResourceAsStream(PLAYER_IMAGE_PATH));
            playerRight = new Image(getClass().getResourceAsStream(PLAYER_RIGHT_IMAGE_PATH));
            playerLeft = new Image(getClass().getResourceAsStream(PLAYER_LEFT_IMAGE_PATH));
            rocketImage = new Image(getClass().getResourceAsStream(ROCKET_IMAGE_PATH));
            laserImage = new Image(getClass().getResourceAsStream(LASER_IMAGE_PATH));
            bulletImage = new Image(getClass().getResourceAsStream(BULLET_IMAGE_PATH));
        } catch (Exception e) {
            System.err.println("Ошибка загрузки изображений: " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // Прекращаем выполнение, если ресурсы не загружены
        }
    }

    private void loadLevel(int index, boolean isRespawn) {
        levelIndex = index;
        level = new Level(levelNames.get(index), projectileManager);

        // Сохраняем текущие параметры игрока, если это не возрождение
        int currentHealth = (player != null && player.getHealth() > 0 && !isRespawn) ? player.getHealth() : 100;
        String currentWeapon = (player != null && !isRespawn) ? player.getActiveWeapon() : null;
        int currentScore = (player != null && !isRespawn) ? player.getScore() : 0;

        if (isRespawn) {
            playerInventory.getItems().clear();
            System.out.println("Инвентарь очищен при возрождении");
            currentScore = 0; // Сбрасываем очки при возрождении
        }

        player = new Player((double) level.getStartX(), (double) level.getStartY(), level,
                bulletImage, laserImage, rocketImage, playerLeft, playerRight, playerInventory, projectileManager);
        player.setHealth(currentHealth);
        player.setActiveWeapon(currentWeapon);
        player.setScore(currentScore);

        projectileManager.setTiles(level.getTiles());
        projectileManager.setEnemies(level.getEnemies());
        projectileManager.setPlayer(player);
        portal = new Portal(level.getPortalX(), level.getPortalY());
        enemyManager = new EnemyManager(this);
        for (Enemy enemy : level.getEnemies()) {
            enemyManager.addEnemy(enemy);
        }

        // Создаём или обновляем игровую сцену
        if (gameScene == null) {
            createGameScene();
        } else {
            updateGameScene();
        }
        primaryStage.setScene(gameScene);
        startGameLoop();
    }

    public void loseLife(String reason) {
        lives--;
        System.out.println(reason + ". Осталось жизней: " + lives);
        stopGameLoop();

        if (lives <= 0) {
            backgroundMusic.stop();
            showLoseScreen();
        } else {
            // Анимация затемнения перед возрождением
            FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_DURATION_MS), gameRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                Timeline delay = new Timeline(new KeyFrame(Duration.seconds(DEATH_DELAY_SECONDS), event -> {
                    loadLevel(levelIndex, true);
                    // Анимация появления после возрождения
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(FADE_DURATION_MS), gameRoot);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                }));
                delay.play();
            });
            fadeOut.play();
        }
    }

    private void update() {
        if (isGamePaused) return;
        player.update(keysPressed);
        level.update(player);
        level.updatePickups(player);
        enemyManager.update(player, level.getTiles());
        projectileManager.updateProjectiles();
        checkCollisions();
        checkLevelCompletion();

        // Обновляем рекорд
        highScore = Math.max(highScore, player.getScore());
    }

    private void checkCollisions() {
        for (Tile tile : level.getTiles()) {
            if (tile.checkCollision(player)) {
                loseLife("Наступил на шипы");
                return;
            }
        }
        if (player.getY() > HEIGHT) {
            loseLife("Упал с карты");
        }
    }

    private void checkLevelCompletion() {
        if (portal.checkCollision(player)) {
            levelIndex++;
            if (levelIndex < levelNames.size()) {
                loadLevel(levelIndex, false);
            } else {
                backgroundMusic.stop();
                showWinScreen();
                stopGameLoop();
            }
        }
    }

    private void render() {
        if (gc == null) {
            System.err.println("GraphicsContext не инициализирован!");
            return;
        }
        gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);
        level.render(gc);
        portal.render(gc);
        player.render(gc);
        enemyManager.render(gc);
        projectileManager.render(gc);

        // Настройка стиля текста с тенью
        DropShadow shadow = new DropShadow(2, 2, 2, Color.BLACK);
        gc.setEffect(shadow);
        gc.setFont(new Font("Arial", 20));
        gc.setFill(Color.WHITE);
        gc.fillText("❤️ Жизни: " + lives, 10, 25);
        gc.fillText("⭐ Очки: " + player.getScore(), 10, 50);
        gc.fillText("🏆 Рекорд: " + highScore, 10, 75);
        gc.fillText("🌍 Уровень: " + (levelIndex + 1) + "/" + levelNames.size(), 10, 100);
        if (player.getActiveWeapon() != null) {
            gc.fillText("🔫 Оружие: " + player.getActiveWeapon(), 10, 125);
        }

        // Полоса здоровья
        gc.setEffect(null); // Убираем тень для полосы здоровья
        gc.setFill(Color.RED);
        double healthBarWidth = 100 * ((double) player.getHealth() / 100);
        gc.fillRect(10, 140, healthBarWidth, 10);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(10, 140, 100, 10);
        gc.setEffect(shadow);
        gc.setFill(Color.WHITE);
        gc.fillText("HP: " + player.getHealth(), 120, 148);
        gc.setEffect(null);
    }

    private void startGameLoop() {
        if (gameLoop == null) {
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (!isGamePaused) {
                        update();
                        render();
                    }
                }
            };
        }
        gameLoop.start();
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

        Text winText = new Text("🎉 ПОБЕДА! Все уровни пройдены!\nОчки: " + player.getScore() + "\nРекорд: " + highScore);
        winText.setFill(Color.GREEN);
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
        exitButton.setOnAction(e -> {
            saveProgress();
            System.exit(0);
        });

        root.getChildren().addAll(winText, restartButton, exitButton);
        winStage.setScene(scene);
        winStage.show();
    }

    private void showLoseScreen() {
        Stage loseStage = new Stage();
        loseStage.setTitle("Поражение!");

        Group root = new Group();
        Scene scene = new Scene(root, 400, 250, Color.BLACK);

        Text loseText = new Text("💀 Вы проиграли! Все жизни потеряны.\nОчки: " + player.getScore() + "\nРекорд: " + highScore);
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
        exitButton.setOnAction(e -> {
            saveProgress();
            System.exit(0);
        });

        root.getChildren().addAll(loseText, restartButton, exitButton);
        loseStage.setScene(scene);
        loseStage.show();
    }

    private void restartGame() {
        levelIndex = 0;
        lives = 3;
        highScore = 0; // Сбрасываем рекорд при полном перезапуске
        keysPressed.clear();
        if (!isMusicMuted) backgroundMusic.play();
        playerInventory = new Inventory();
        loadLevel(levelIndex, false);
        primaryStage.setScene(gameScene);
        startGameLoop();
    }

    private void createGameScene() {
        gameRoot = new Group();
        gameScene = new Scene(gameRoot);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        gameRoot.getChildren().add(canvas);
        gc = canvas.getGraphicsContext2D();

        gameScene.setOnKeyPressed(e -> {
            keysPressed.add(e.getCode());
            switch (e.getCode()) {
                case F:
                    player.shoot();
                    break;
                case E:
                    isGamePaused = true;
                    stopGameLoop();
                    showInventoryMenu(primaryStage);
                    break;
                case H:
                    level.revealHiddenTiles(player);
                    break;
                case DIGIT1:
                    selectWeapon("bullet");
                    break;
                case DIGIT2:
                    selectWeapon("rocket");
                    break;
                case DIGIT3:
                    selectWeapon("laser");
                    break;
            }
        });

        gameScene.setOnKeyReleased(e -> keysPressed.remove(e.getCode()));

        Button pauseButton = new Button("⏸ Пауза");
        pauseButton.setLayoutX(WIDTH - 90);
        pauseButton.setLayoutY(10);
        pauseButton.setStyle("-fx-font-size: 14px; -fx-background-color: #555555; -fx-text-fill: white;");
        pauseButton.setOnAction(e -> {
            isGamePaused = !isGamePaused;
            pauseButton.setText(isGamePaused ? "▶ Возобновить" : "⏸ Пауза");
            if (!isGamePaused) startGameLoop();
            else stopGameLoop();
        });
        pauseButton.setFocusTraversable(false);

        gameRoot.getChildren().add(pauseButton);
    }

    private void selectWeapon(String weaponType) {
        for (Item item : player.getInventory().getItems()) {
            if ((item instanceof AmmoBullet && weaponType.equals("bullet")) ||
                    (item instanceof AmmoRocket && weaponType.equals("rocket")) ||
                    (item instanceof AmmoLaser && weaponType.equals("laser"))) {
                item.use(player);
                System.out.println("Выбрано оружие: " + weaponType);
                return;
            }
        }
        System.out.println("Оружие " + weaponType + " не найдено в инвентаре");
        player.setActiveWeapon(null);
    }

    private void updateGameScene() {
        if (gc == null) {
            Canvas canvas = (Canvas) gameRoot.getChildren().get(0);
            gc = canvas.getGraphicsContext2D();
        }
    }

    private void showMainMenu(Stage stage) {
        Group root = new Group();
        menuScene = new Scene(root, WIDTH, HEIGHT);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);

        Text title = new Text("🌌 SPACE ESCAPE");
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        title.setX(250);
        title.setY(150);
        title.setEffect(new DropShadow(5, 3, 3, Color.BLACK));

        Button startButton = new Button("▶ Начать игру");
        startButton.setLayoutX(330);
        startButton.setLayoutY(220);
        startButton.setStyle("-fx-font-size: 16px; -fx-background-color: #444444; -fx-text-fill: white;");
        startButton.setOnAction(e -> {
            loadLevel(levelIndex, false);
            stage.setScene(gameScene);
            startGameLoop();
        });

        Button exitButton = new Button("❌ Выйти");
        exitButton.setLayoutX(345);
        exitButton.setLayoutY(270);
        exitButton.setStyle("-fx-font-size: 16px; -fx-background-color: #444444; -fx-text-fill: white;");
        exitButton.setOnAction(e -> {
            saveProgress();
            System.exit(0);
        });

        Button settingsButton = new Button("⚙ Настройки");
        settingsButton.setLayoutX(330);
        settingsButton.setLayoutY(320);
        settingsButton.setStyle("-fx-font-size: 16px; -fx-background-color: #444444; -fx-text-fill: white;");
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

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        Text settingsTitle = new Text("Настройки");
        settingsTitle.setFill(Color.WHITE);
        settingsTitle.setStyle("-fx-font-size: 30px;");
        settingsTitle.setX(300);
        settingsTitle.setY(100);
        settingsTitle.setEffect(new DropShadow(3, 2, 2, Color.BLACK));

        Button soundButton = new Button(isSoundMuted ? "🔊 Включить звук" : "🔇 Выключить звук");
        soundButton.setLayoutX(300);
        soundButton.setLayoutY(150);
        soundButton.setStyle("-fx-font-size: 16px; -fx-background-color: #444444; -fx-text-fill: white;");
        soundButton.setOnAction(e -> {
            isSoundMuted = !isSoundMuted;
            if (player != null) {
                player.setJumpSoundMuted(isSoundMuted);
            }
            soundButton.setText(isSoundMuted ? "🔊 Включить звук" : "🔇 Выключить звук");
        });

        Button musicButton = new Button(isMusicMuted ? "🎶 Включить музыку" : "🔇 Выключить музыку");
        musicButton.setLayoutX(300);
        musicButton.setLayoutY(200);
        musicButton.setStyle("-fx-font-size: 16px; -fx-background-color: #444444; -fx-text-fill: white;");
        musicButton.setOnAction(e -> {
            isMusicMuted = !isMusicMuted;
            backgroundMusic.setMute(isMusicMuted);
            musicButton.setText(isMusicMuted ? "🎶 Включить музыку" : "🔇 Выключить музыку");
        });

        Button backButton = new Button("↩ Назад");
        backButton.setLayoutX(350);
        backButton.setLayoutY(270);
        backButton.setStyle("-fx-font-size: 16px; -fx-background-color: #444444; -fx-text-fill: white;");
        backButton.setOnAction(e -> stage.setScene(menuScene));

        root.getChildren().addAll(settingsTitle, soundButton, musicButton, backButton);
        stage.setScene(settingsScene);
    }

    private void showInventoryMenu(Stage stage) {
        Group root = new Group();
        inventoryScene = new Scene(root, WIDTH, HEIGHT);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        Text inventoryTitle = new Text("Инвентарь");
        inventoryTitle.setFill(Color.WHITE);
        inventoryTitle.setStyle("-fx-font-size: 30px;");
        inventoryTitle.setX(300);
        inventoryTitle.setY(100);
        inventoryTitle.setEffect(new DropShadow(3, 2, 2, Color.BLACK));

        List<Item> items = player.getInventory().getItems();
        System.out.println("Предметы в инвентаре: " + items.size());
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String itemText = item.getName();
            if (item instanceof HealthPack) {
                itemText += ": Восстанавливает " + ((HealthPack) item).getHealAmount() + " здоровья";
            } else if (item instanceof AmmoBullet) {
                itemText += ": Оружие (Пули, " + ((AmmoBullet) item).getQuantity() + ")";
            } else if (item instanceof AmmoRocket) {
                itemText += ": Оружие (Ракеты, " + ((AmmoRocket) item).getQuantity() + ")";
            } else if (item instanceof AmmoLaser) {
                itemText += ": Оружие (Лазер, " + ((AmmoLaser) item).getQuantity() + ")";
            }
            Text itemLabel = new Text(itemText);
            itemLabel.setFill(Color.WHITE);
            itemLabel.setStyle("-fx-font-size: 20px;");
            itemLabel.setX(200);
            itemLabel.setY(150 + i * 30);
            root.getChildren().add(itemLabel);

            Button useButton = new Button("Использовать");
            useButton.setLayoutX(500);
            useButton.setLayoutY(130 + i * 30);
            useButton.setStyle("-fx-font-size: 14px; -fx-background-color: #444444; -fx-text-fill: white;");
            int index = i;
            useButton.setOnAction(e -> {
                Item selectedItem = items.get(index);
                selectedItem.use(player);
                if (!(selectedItem instanceof AmmoBullet || selectedItem instanceof AmmoRocket || selectedItem instanceof AmmoLaser)) {
                    player.getInventory().getItems().remove(selectedItem);
                }
                showInventoryMenu(stage);
            });
            root.getChildren().add(useButton);
        }

        Button backButton = new Button("↩ Назад");
        backButton.setLayoutX(350);
        backButton.setLayoutY(HEIGHT - 50);
        backButton.setStyle("-fx-font-size: 16px; -fx-background-color: #444444; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            isGamePaused = false;
            stage.setScene(gameScene);
            startGameLoop();
        });

        root.getChildren().addAll(inventoryTitle, backButton);
        stage.setScene(inventoryScene);

        inventoryScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.E) {
                isGamePaused = false;
                stage.setScene(gameScene);
                startGameLoop();
            }
        });
    }

    private void saveProgress() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PROGRESS_FILE))) {
            oos.writeInt(levelIndex);
            oos.writeInt(highScore);
            oos.writeBoolean(isMusicMuted);
            oos.writeBoolean(isSoundMuted);
            System.out.println("Прогресс сохранён: Уровень " + levelIndex + ", Рекорд " + highScore);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения прогресса: " + e.getMessage());
        }
    }

    private void loadProgress() {
        File file = new File(PROGRESS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PROGRESS_FILE))) {
                levelIndex = ois.readInt();
                highScore = ois.readInt();
                isMusicMuted = ois.readBoolean();
                isSoundMuted = ois.readBoolean();
                System.out.println("Прогресс загружен: Уровень " + levelIndex + ", Рекорд " + highScore);
            } catch (IOException e) {
                System.err.println("Ошибка загрузки прогресса: " + e.getMessage());
                levelIndex = 0;
                highScore = 0;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}