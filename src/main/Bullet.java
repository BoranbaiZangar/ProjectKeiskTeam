package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bullet extends Projectile {
    public Bullet(double x, double y, boolean facingRight, Image image) {
        super(x, y, facingRight ? 6 : -6, 0, image); // Движение только по X
        this.width = 10;
        this.height = 10;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }
}