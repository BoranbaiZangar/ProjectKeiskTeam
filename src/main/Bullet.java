package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Bullet extends Projectile {

    private double dx;

    public Bullet(double x, double y, boolean facingRight, Image image) {
        super(x, y, facingRight ? 6 : -6, image); // передаем dx в родителя
        this.dx = facingRight ? 6 : -6;
        this.width = 10;
        this.height = 10;
    }

    @Override
    public void update() {
        x += dx; // 🔄 теперь пуля летит в сторону
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height); // Отрисовка с нормальным размером
    }
}
