package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Rocket extends Projectile {



    public Rocket(double x, double y, boolean facingRight, Image image) {
        super(x, y, facingRight ? 3 : -3, image);
        this.width = 27;
        this.height = 25;
    }

    @Override
    public void update() {
        x += speed;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height); // чуть крупнее пули
    }
}
