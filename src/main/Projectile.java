package main;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

public abstract class Projectile {
    protected double x, y;
    protected double speedX, speedY; // Поддержка движения по X и Y
    protected Image image;
    protected double width = 10;
    protected double height = 10;

    public Projectile(double startX, double startY, double speedX, double speedY, Image image) {
        this.x = startX;
        this.y = startY;
        this.speedX = speedX;
        this.speedY = speedY;
        this.image = image;
    }

    public void update() {
        x += speedX;
        y += speedY;
    }

    public abstract void render(GraphicsContext gc);

    public boolean intersects(Tile tile) {
        return tile.getBounds().intersects(x, y, width, height);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isOutOfBounds(double screenWidth, double screenHeight) {
        return x < 0 || x > screenWidth || y < 0 || y > screenHeight;
    }
    public double getWidth() {
        return width;
    }

    public Bounds getBounds() {
        return new Rectangle(x, y, width, height).getBoundsInLocal();
    }
}