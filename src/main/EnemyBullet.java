package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class EnemyBullet extends Projectile {
    public EnemyBullet(double x, double y, boolean facingRight, Image image) {
        super(x, y, facingRight ? 4 : -4, 0, image);
        this.width = 8;
        this.height = 8;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }
}