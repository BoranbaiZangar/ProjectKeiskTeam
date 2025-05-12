package main;

import javafx.scene.image.Image;

public class Laser extends Projectile {

    public Laser(double startX, double startY, Image laserImage) {
        super(startX, startY, 10, laserImage);  // 10 — скорость лазера
    }

    @Override
    public void update() {
        super.update();  // Лазер движется вверх
    }
}
