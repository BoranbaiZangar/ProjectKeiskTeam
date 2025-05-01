package main;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Tile {

    public enum Type {
        PLATFORM,
        SPIKES,
        ICE,
        DISAPPEARING
    }

    private int x, y;
    private Type type;
    private Image image;

    public Tile(int x, int y, Type type, Image image) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.image = image;
    }

    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, x, y, Level.TILE_SIZE, Level.TILE_SIZE);
        }
    }

    public Type getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

