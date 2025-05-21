package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public abstract class Enemy {
    protected double x, y, width, height;
    protected enum State { PATROL, ALERT, ATTACK }
    protected State state = State.PATROL;
    protected Image image;
    protected double speed = 1.5;
    protected double patrolRange = 100;
    protected double initialX;
    protected boolean facingRight = true;
    protected int health = 50; // Начальное здоровье врага
    protected boolean isDead = false; // Флаг смерти

    public Enemy(double x, double y, Image image) {
        this.x = x;
        this.y = y;
        this.initialX = x;
        this.width = 30;
        this.height = 35;
        this.image = image;
    }

    public void update(Player player, List<Tile> tiles) {
        if (isDead) return; // Не обновляем мёртвого врага
        switch (state) {
            case PATROL:
                patrol();
                if (detectPlayer(player)) state = State.ALERT;
                break;
            case ALERT:
                if (Math.abs(player.getX() - x) < 100) state = State.ATTACK;
                else state = State.PATROL;
                break;
            case ATTACK:
                attack(player);
                break;
        }
        // Простая гравитация и столкновения
        double nextX = x + (facingRight ? speed : -speed);
        if (!isColliding(nextX, y, tiles)) x = nextX;
        else facingRight = !facingRight;
    }

    protected void patrol() {
        if (facingRight && x >= initialX + patrolRange) facingRight = false;
        else if (!facingRight && x <= initialX - patrolRange) facingRight = true;
    }

    protected abstract void attack(Player player);

    protected boolean detectPlayer(Player player) {
        double distance = Math.abs(player.getX() - x);
        return distance < 200 && Math.abs(player.getY() - y) < 50;
    }

    private boolean isColliding(double nextX, double nextY, List<Tile> tiles) {
        for (Tile tile : tiles) {
            if (tile.getType() == Tile.Type.PLATFORM || tile.getType() == Tile.Type.ICE) {
                if (nextX + width > tile.getX() &&
                        nextX < tile.getX() + tile.getSize() &&
                        nextY + height > tile.getY() &&
                        nextY < tile.getY() + tile.getSize()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            isDead = true;
            System.out.println("Враг убит на позиции: (" + x + ", " + y + ")");
        }
    }

    public void render(GraphicsContext gc) {
        if (!isDead) {
            gc.drawImage(image, x, y, width, height);
            // Отображение HP над врагом
            gc.setFill(Color.RED);
            double healthBarWidth = 30 * ((double) health / 50);
            gc.fillRect(x, y - 10, healthBarWidth, 5);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y - 10, 30, 5);
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isDead() { return isDead; }
}