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
    private final double width = 40;
    private final double height = 40;

    private double velocityY = 0;
    private boolean canJump = false;

    private final double MOVE_SPEED = 5;
    private final double JUMP_FORCE = -15;
    private final double GRAVITY = 0.8;

    private final double GROUND_Y;
    private List<Tile> tiles;

    private Image playerImage;
    private AudioClip jumpSound;

    public Player(double startX, double startY, double groundY, List<Tile> tiles) {
        this.x = startX;
        this.y = startY;
        this.GROUND_Y = groundY;
        this.tiles = tiles;

        playerImage = new Image(getClass().getResourceAsStream("/images/player.png"));
        jumpSound = new AudioClip(getClass().getResource("/sounds/jump.wav").toString());
    }

    public void update(Set<KeyCode> keysPressed) {
        double nextX = x;
        if (keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.A)) {
            nextX -= MOVE_SPEED;
        }
        if (keysPressed.contains(KeyCode.RIGHT) || keysPressed.contains(KeyCode.D)) {
            nextX += MOVE_SPEED;
        }

        if ((keysPressed.contains(KeyCode.UP) || keysPressed.contains(KeyCode.W) || keysPressed.contains(KeyCode.SPACE)) && canJump) {
            velocityY = JUMP_FORCE;
            canJump = false;
            jumpSound.play();
        }

        velocityY += GRAVITY;
        double nextY = y + velocityY;

        if (!isColliding(nextX, y)) {
            x = nextX;
        }

        if (!isColliding(x, nextY)) {
            y = nextY;
        } else {
            if (velocityY > 0) {
                canJump = true;
            }
            velocityY = 0;
        }

        if (y + height >= GROUND_Y) {
            y = GROUND_Y - height;
            velocityY = 0;
            canJump = true;
        }
    }

    private boolean isColliding(double nextX, double nextY) {
        for (Tile tile : tiles) {
            if (tile.getType() == Tile.Type.PLATFORM) {
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
