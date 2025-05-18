package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Laser extends Projectile {
    public Laser(double x, double y, boolean facingRight, Image image) {
        super(x, y, facingRight ? 8 : -8, 0, image);
        this.width = 90;
        this.height = 4;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }
}