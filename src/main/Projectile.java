package main;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;


public abstract class Projectile {

    protected double x, y;
    protected double speed;
    protected Image image;

    protected double width = 10;
    protected double height = 10;
    public Projectile(double startX, double startY, double speed, Image image) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.image = image;
    }

    public void update() {
        y -= speed;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y);
    }

    public boolean intersects(Tile tile) {
        return tile.getBounds().intersects(x, y, image.getWidth(), image.getHeight());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    public boolean isOutOfBounds(double screenHeight) {
        return y < 0 || y > screenHeight;
    }
    public Bounds getBounds() {
        return new Rectangle(x, y, width, height).getBoundsInLocal();
    }

}
