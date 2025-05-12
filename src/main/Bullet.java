package main;

import javafx.scene.image.Image;

public class Bullet extends Projectile {

    public Bullet(double startX, double startY, Image bulletImage) {
        super(startX, startY, 5, bulletImage);  // 5 — скорость пули
    }

    @Override
    public void update() {
        super.update();  // Пуля движется вверх
    }
}
