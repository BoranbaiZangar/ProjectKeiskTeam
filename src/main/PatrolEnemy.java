package main;

import javafx.scene.image.Image;

public class PatrolEnemy extends Enemy {
    private ProjectileManager projectileManager;
    private double shootCooldown = 0;
    private static final double SHOOT_DELAY = 1.0; // Стрельба раз в секунду
    private Image bulletImage;

    public PatrolEnemy(double x, double y, Image image, ProjectileManager projectileManager) {
        super(x, y, image);
        this.projectileManager = projectileManager;
        this.bulletImage = new Image(getClass().getResourceAsStream("/images/bullet.png"));
    }

    @Override
    protected void attack(Player player) {
        if (player.getX() > x) facingRight = true;
        else facingRight = false;

        // Стрельба
        if (shootCooldown <= 0) {
            double offsetX = facingRight ? width : -8;
            projectileManager.addProjectile(new EnemyBullet(x + offsetX, y + height / 2, facingRight, bulletImage));
            shootCooldown = SHOOT_DELAY;
            System.out.println("Враг выстрелил на позиции: (" + x + ", " + y + ")");
        } else {
            shootCooldown -= 1.0 / 60; // Предполагаем 60 FPS
        }
    }
}