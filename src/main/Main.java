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

    private boolean isMusicMuted = false;
    private boolean isSoundMuted = false;

    private final Set<KeyCode> keysPressed = new HashSet<>();
    private Player player;
    private Level level;
    private Portal portal;
    private boolean isGamePaused = false;
    private Image backgroundImage;
    private MediaPlayer backgroundMusic;
    private Inventory playerInventory;

    private final List<String> levelNames = List.of("level1.txt", "level2.txt", "level3.txt", "level4.txt");
    private int levelIndex = 0;

    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private int lives = 3;

    private Scene menuScene;
    private Scene settingsScene;
    private Scene inventoryScene;
    private Scene gameScene;
    private Group gameRoot;
    private Stage primaryStage;

    private Image laserImage;
    private Image rocketImage;
    private Image playerImage;
    private Image bulletImage;
    private Image playerRight;
    private Image playerLeft;

    private EnemyManager enemyManager;
    private ProjectileManager projectileManager;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Space Escape");
        primaryStage.setResizable(false);
        projectileManager = new ProjectileManager();
        playerInventory = new Inventory();

        try {
            playerImage = new Image(getClass().getResourceAsStream("/images/player.png"));
            playerRight = new Image(getClass().getResourceAsStream("/images/player_right.png"));
            playerLeft = new Image(getClass().getResourceAsStream("/images/player_left.png"));
            rocketImage = new Image(getClass().getResourceAsStream("/images/rocket.png"));
            laserImage = new Image(getClass().getResourceAsStream("/images/laser.png"));
            bulletImage = new Image(getClass().getResourceAsStream("/images/bullet.png"));
            backgroundImage = new Image(getClass().getResourceAsStream("/images/background.png"));
        } catch (Exception e) {
            System.err.println("Ошибка загрузки изображений: " + e.getMessage());
            e.printStackTrace();
        }

        Media media = new Media(getClass().getResource("/sounds/background-music.mp3").toString());
        backgroundMusic = new MediaPlayer(media);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.play();

        loadLevel(levelIndex);
        showMainMenu(primaryStage);
        primaryStage.show();
    }

    private void loadLevel(int index) {
        level = new Level(levelNames.get(index), projectileManager);
        int currentHealth = (player != null) ? player.getHealth() : 100;
        String currentWeapon = (player != null) ? player.getActiveWeapon() : null;
        int currentScore = (player != null) ? player.getScore() : 0;

        player = new Player((double) level.getStartX(), (double) level.getStartY(), level,
                bulletImage, laserImage, rocketImage,
                playerLeft, playerRight, playerInventory);
        player.setHealth(currentHealth);
        player.setActiveWeapon(currentWeapon);
        player.setScore(currentScore);

        projectileManager.setTiles(level.getTiles());
        portal = new Portal(level.getPortalX(), level.getPortalY());
        enemyManager = new EnemyManager();
        for (Enemy enemy : level.getEnemies()) {
            enemyManager.addEnemy(enemy);
        }
        if (gameScene != null) {
            updateGameScene();
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
                loadLevel(levelIndex);
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
        gc.setFill(Color.WHITE);
        gc.fillText("❤️ Жизни: " + lives, 10, 25);
        gc.fillText("⭐ Очки: " + player.getScore(), 10, 75);
        if (player.getActiveWeapon() != null) {
            gc.fillText("Оружие: " + player.getActiveWeapon(), 10, 50);
        }
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
            new Thread(() -> {
                try {
                    Thread.sleep(DEATH_DELAY);
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

        Text winText = new Text("🎉 ПОБЕДА! Все уровни пройдены!\nОчки: " + player.getScore());
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

        root.getChildren().addAll(winText, restartButton, exitButton);
        winStage.setScene(scene);
        winStage.show();
    }

    private void showLoseScreen() {
        Stage loseStage = new Stage();
        loseStage.setTitle("Поражение!");

        Group root = new Group();
        Scene scene = new Scene(root, 400, 250, Color.BLACK);

        Text loseText = new Text("💀 Вы проиграли! Все жизни потеряны.\nОчки: " + player.getScore());
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
        playerInventory = new Inventory();
        loadLevel(levelIndex);
        gameLoop.start();
        primaryStage.setScene(gameScene);
    }

    private void createGameScene() {
        if (gameScene == null) {
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
            pauseButton.setOnAction(e -> {
                isGamePaused = !isGamePaused;
                pauseButton.setText(isGamePaused ? "▶ Возобновить" : "⏸ Пауза");
            });
            pauseButton.setFocusTraversable(false);

            gameRoot.getChildren().add(pauseButton);

            gameLoop = new AnimationTimer() {
                @Override public void handle(long now) {
                    if (!isGamePaused) {
                        update();
                        render();
                    }
                }
            };
        }
        updateGameScene();
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

        if (backgroundImage == null)
            backgroundImage = new Image(getClass().getResourceAsStream("/images/background.png"));
        gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);

        Text title = new Text("🌌 SPACE ESCAPE");
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        title.setX(250);
        title.setY(150);

        Button startButton = new Button("▶ Начать игру");
        startButton.setLayoutX(330);
        startButton.setLayoutY(220);
        startButton.setOnAction(e -> {
            createGameScene();
            stage.setScene(gameScene);
            gameLoop.start();
        });

        Button exitButton = new Button("❌ Выйти");
        exitButton.setLayoutX(345);
        exitButton.setLayoutY(270);
        exitButton.setOnAction(e -> System.exit(0));

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

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        Text settingsTitle = new Text("Настройки");
        settingsTitle.setFill(Color.WHITE);
        settingsTitle.setStyle("-fx-font-size: 30px;");
        settingsTitle.setX(300);
        settingsTitle.setY(100);

        Button soundButton = new Button(isSoundMuted ? "🔊 Включить звук" : "🔇 Выключить звук");
        soundButton.setLayoutX(300);
        soundButton.setLayoutY(150);
        soundButton.setOnAction(e -> {
            isSoundMuted = !isSoundMuted;
            if (isSoundMuted) {
                backgroundMusic.setMute(true);
                if (player != null) {
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

        Button backButton = new Button("↩ Назад");
        backButton.setLayoutX(350);
        backButton.setLayoutY(270);
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
        backButton.setOnAction(e -> {
            isGamePaused = false;
            stage.setScene(gameScene);
        });

        root.getChildren().addAll(inventoryTitle, backButton);
        stage.setScene(inventoryScene);

        inventoryScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.E) {
                isGamePaused = false;
                stage.setScene(gameScene);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}