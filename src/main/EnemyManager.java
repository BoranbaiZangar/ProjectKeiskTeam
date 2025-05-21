package main;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemyManager {
    private List<Enemy> enemies = new ArrayList<>();
    private Main game; // Ссылка на Main

    public EnemyManager(Main game) {
        this.game = game;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void update(Player player, List<Tile> tiles) {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.isDead()) {
                iterator.remove();
                continue;
            }
            enemy.update(player, tiles);
            // Проверка столкновения с игроком
            if (!enemy.isDead() &&
                    player.getX() + player.getWidth() > enemy.getX() &&
                    player.getX() < enemy.getX() + enemy.getWidth() &&
                    player.getY() + player.getHeight() > enemy.getY() &&
                    player.getY() < enemy.getY() + enemy.getHeight()) {
                player.setHealth(player.getHealth() - 10); // Урон от контакта
                System.out.println("Игрок получил урон от врага. Здоровье: " + player.getHealth());
                if (player.getHealth() <= 0) {
                    game.loseLife("Убит врагом");
                }
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (Enemy enemy : enemies) {
            enemy.render(gc);
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}