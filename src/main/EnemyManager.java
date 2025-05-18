package main;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class EnemyManager {
    private List<Enemy> enemies = new ArrayList<>();

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void update(Player player, List<Tile> tiles) {
        for (Enemy enemy : enemies) {
            enemy.update(player, tiles);
            // Проверка столкновения с игроком
            if (player.getX() + player.getWidth() > enemy.getX() &&
                    player.getX() < enemy.getX() + enemy.getWidth() &&
                    player.getY() + player.getHeight() > enemy.getY() &&
                    player.getY() < enemy.getY() + enemy.getHeight()) {
                // Игрок получает урон
                // Вызывать loseLife из Main
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (Enemy enemy : enemies) {
            enemy.render(gc);
        }
    }
}