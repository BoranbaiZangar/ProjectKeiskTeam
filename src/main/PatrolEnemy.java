package main;

import javafx.scene.image.Image;

public class PatrolEnemy extends Enemy {
    public PatrolEnemy(double x, double y, Image image) {
        super(x, y, image);
    }

    @Override
    protected void attack(Player player) {
        // Например, стрельба в игрока
        if (player.getX() > x) facingRight = true;
        else facingRight = false;
        // Можно добавить создание снаряда
    }
}