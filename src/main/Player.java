package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.AudioClip;

import java.util.List;
import java.util.Set;

public class Player {

    private double x;
    private double y;
    private final double width = 30;
    private final double height = 35;

    private double velocityY = 0;
    private boolean canJump = false;

    private final double MOVE_SPEED = 2.5;
    private final double JUMP_FORCE = -12;
    private final double GRAVITY = 0.5;

    private final double GROUND_Y;
    private List<Tile> tiles;

    private Image playerImage;
    private AudioClip jumpSound;

    private double velocityX = 0;
    private boolean onIce = false;


    public Player(double startX, double startY, double groundY, List<Tile> tiles) {
        this.x = startX;
        this.y = startY;
        this.GROUND_Y = groundY;
        this.tiles = tiles;

        playerImage = new Image(getClass().getResourceAsStream("/images/player.png"));
        jumpSound = new AudioClip(getClass().getResource("/sounds/jump.wav").toString());
    }

    public void update(Set<KeyCode> keysPressed) {
        // Управление влево/вправо
        if (keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.A)) {
            velocityX = -MOVE_SPEED;
        } else if (keysPressed.contains(KeyCode.RIGHT) || keysPressed.contains(KeyCode.D)) {
            velocityX = MOVE_SPEED;
        } else if (!onIce) {
            velocityX = 0; // мгновенная остановка
        } else {
            // Если на льду, замедляем
            velocityX *= 0.95;
            if (Math.abs(velocityX) < 0.1) velocityX = 0;
        }

        // Прыжок
        if ((keysPressed.contains(KeyCode.UP) || keysPressed.contains(KeyCode.W) || keysPressed.contains(KeyCode.SPACE)) && canJump) {
            velocityY = JUMP_FORCE;
            canJump = false;
            jumpSound.play();
        }

        velocityY += GRAVITY;

        // Сброс флага льда
        onIce = false;

        // Проверка под ногами
        for (Tile tile : tiles) {
            double tx = tile.getX();
            double ty = tile.getY();
            double footX = x + width / 2;
            double footY = y + height + 1;

            boolean standingOnTile = footX > tx && footX < tx + Level.TILE_SIZE &&
                    footY > ty && footY < ty + Level.TILE_SIZE;

            if (standingOnTile && tile.getType() == Tile.Type.ICE) {
                onIce = true;
            }

            // Исчезающие платформы
            if (tile.getType() == Tile.Type.DISAPPEARING) {
                double dx = tile.getX();
                double dy = tile.getY();
                double foodX = x + width / 2;
                double foodY = y + height + 1;

                boolean onTop = foodX > dx && foodX < dx + Level.TILE_SIZE &&
                        foodY > dy && foodY < dy + Level.TILE_SIZE;

                if (onTop) {
                    tile.triggerDisappear();
                }
            }


        }

        // Движение
        double nextX = x + velocityX;
        double nextY = y + velocityY;

        if (!isColliding(nextX, y)) x = nextX;
        if (!isColliding(x, nextY)) y = nextY;
        else {
            if (velocityY > 0) canJump = true;
            velocityY = 0;
        }

        // Земля
        if (y + height >= GROUND_Y) {
            y = GROUND_Y - height;
            velocityY = 0;
            canJump = true;
        }

        // Ограничение по границам экрана
        if (x < 0) x = 0;
        if (x + width > Main.WIDTH) x = Main.WIDTH - width;

        if (y < 0) y = 0;
        if (y + height > Main.HEIGHT) y = Main.HEIGHT - height;

    }




    private boolean isColliding(double nextX, double nextY) {
        for (Tile tile : tiles) {
            Tile.Type type = tile.getType();
            if (type == Tile.Type.PLATFORM || type == Tile.Type.ICE || type == Tile.Type.DISAPPEARING) {
                double tx = tile.getX();
                double ty = tile.getY();

                boolean overlap = nextX + width > tx &&
                        nextX < tx + Level.TILE_SIZE &&
                        nextY + height > ty &&
                        nextY < ty + Level.TILE_SIZE;

                if (overlap) return true;
            }
        }
        return false;
    }


    public void render(GraphicsContext gc) {
        gc.drawImage(playerImage, x, y, width, height);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
