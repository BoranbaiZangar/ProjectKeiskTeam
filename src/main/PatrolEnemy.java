package main;

import javafx.scene.image.Image;

public class PatrolEnemy extends Enemy {
    private ProjectileManager projectileManager;

    public PatrolEnemy(double x, double y, Image image, ProjectileManager projectileManager) {
        super(x, y, image);
        this.projectileManager = projectileManager;
    }

    @Override
    protected void attack(Player player) {
        if (player.getX() > x) facingRight = true;
        else facingRight = false;
        // Стрельба будет добавлена в следующей задаче
    }
}