package main;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile {

    public enum Type {
        PLATFORM,
        SPIKES,
        ICE,
        DISAPPEARING,
        EMPTY // для исчезнувших тайлов
    }

    private int x, y;
    private Type type;
    private Image image;
    private double size = Level.TILE_SIZE;

    private boolean markedToDisappear = false;
    private long disappearStartTime = 0;
    private double width, height;

    public Tile(int x, int y, Type type, Image image) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.image = image;
        this.width = size;  // Инициализация ширины на основе изображения
        this.height = size;
    }

    public void render(GraphicsContext gc) {
        if (type == Type.EMPTY) return;

        if (image != null) {
            // Эффект мигания исчезающей платформы
            if (type == Type.DISAPPEARING && markedToDisappear) {
                long now = System.currentTimeMillis();
                long elapsed = now - disappearStartTime;

                // мигает каждые 200мс
                if ((elapsed / 200) % 2 == 0) {
                    gc.drawImage(image, x, y, size, size);
                } else {
                    gc.setFill(Color.DARKRED);
                    gc.fillRect(x, y, size, size);
                }
            } else {
                gc.drawImage(image, x, y, size, size);
            }
        }
    }

    public void update() {
        if (type == Type.DISAPPEARING && markedToDisappear) {
            long now = System.currentTimeMillis();
            if (now - disappearStartTime > 1500) {
                type = Type.EMPTY;
                image = null;
            }
        }
    }

    public void triggerDisappear() {
        if (!markedToDisappear) {
            markedToDisappear = true;
            disappearStartTime = System.currentTimeMillis();
        }
    }

    public boolean intersects(Player player) {
        double px = player.getX();
        double py = player.getY();
        double pw = player.getWidth();
        double ph = player.getHeight();

        return px < x + size &&
                px + pw > x &&
                py < y + size &&
                py + ph > y;
    }
    public Bounds getBounds() {
        return new Rectangle(x, y, width, height).getBoundsInLocal(); // ✅
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

    public double getSize() {
        return size;
    }

}
