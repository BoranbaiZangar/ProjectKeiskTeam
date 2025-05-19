package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Door {
    double x;
    double y;
    private double width = 40, height = 80;
    private boolean isOpen;
    private String keyId;
    private Image image;

    public Door(double x, double y, String keyId, Image image) {
        this.x = x;
        this.y = y;
        this.keyId = keyId;
        this.image = image;
        this.isOpen = false;
    }

    public void open() {
        isOpen = true;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean checkCollision(Player player, Inventory inventory) {
        if (isOpen || (keyId != null && inventory.hasKey(keyId))) {
            return false;
        }
        double px = player.getX();
        double py = player.getY();
        double pw = player.getWidth();
        double ph = player.getHeight();

        return px + pw > x && px < x + width &&
                py + ph > y && py < y + height;
    }

    public void render(GraphicsContext gc) {
        if (!isOpen) {
            gc.drawImage(image, x, y, width, height);
        }
    }

    public String getKeyId() { // Новый геттер
        return keyId;
    }
}