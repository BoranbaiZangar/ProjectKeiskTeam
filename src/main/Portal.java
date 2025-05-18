package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

public class Portal {

    private double x;
    private double y;
    private final double width = 40;
    private final double height = 40;

    private Image portalImage;
    private AudioClip teleportSound;

    public Portal(double x, double y) {
        this.x = x;
        this.y = y;

        portalImage = new Image(getClass().getResourceAsStream("/images/portal.png"));
        teleportSound = new AudioClip(getClass().getResource("/sounds/teleport.wav").toString());
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(portalImage, x, y, width, height);
    }

    public boolean checkCollision(Player player) {

        boolean isColliding = player.getX() + player.getWidth() > x &&
                player.getX() < x + width &&
                player.getY() + player.getHeight() > y &&
                player.getY() < y + height;
        if (isColliding) {
            teleportSound.play();
        }
        return isColliding;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
