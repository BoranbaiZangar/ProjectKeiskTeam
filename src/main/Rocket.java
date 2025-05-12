package main;

import javafx.scene.image.Image;

public class Rocket extends Projectile {

    public Rocket(double startX, double startY, Image rocketImage) {
        super(startX, startY, 3, rocketImage);  // 3 — скорость ракеты
    }

    @Override
    public void update() {
        super.update();  // Ракета движется вверх
    }
}
