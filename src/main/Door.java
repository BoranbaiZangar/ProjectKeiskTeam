package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Door {
    private double x;
    private double y;
    private String id; // Идентификатор двери
    private boolean isOpen;
    private Image image;

    public Door(double x, double y, String id, Image image) {
        this.x = x;
        this.y = y;
        this.id = id; // Может быть null, если дверь не связана с ключом
        this.isOpen = false;
        this.image = image;
    }

    public void render(GraphicsContext gc) {
        if (!isOpen) {
            gc.drawImage(image, x, y, Level.TILE_SIZE, Level.TILE_SIZE);
        }
    }

    public boolean checkCollision(Player player, Inventory inventory) {
        if (isOpen) {
            return false;
        }

        double px = player.getX();
        double py = player.getY();
        double pw = player.getWidth();
        double ph = player.getHeight();

        boolean isColliding = px + pw > x && px < x + Level.TILE_SIZE &&
                py + ph > y && py < y + Level.TILE_SIZE;

        return isColliding;
    }

    public void open() {
        isOpen = true;
    }

    public String getId() {
        return id;
    }

    public boolean isOpen() {
        return isOpen;
    }
}