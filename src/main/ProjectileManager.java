package main;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProjectileManager {
    private final List<Projectile> projectiles = new ArrayList<>();
    private List<Tile> tiles;
    private List<Enemy> enemies;
    private Player player;
    private Main game; // Ссылка на Main

    public ProjectileManager(Main game) {
        this.game = game;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void addProjectile(Projectile p) {
        projectiles.add(p);
    }

    public void updateProjectiles() {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            p.update();

            // Проверка выхода за экран
            if (p.isOutOfBounds(Main.WIDTH, Main.HEIGHT)) {
                iterator.remove();
                continue;
            }

            // Проверка столкновения с тайлами
            if (tiles != null) {
                for (Tile tile : tiles) {
                    if (tile.getType() == Tile.Type.PLATFORM || tile.getType() == Tile.Type.ICE) {
                        if (tile.getBounds().intersects(p.getBounds())) {
                            iterator.remove();
                            break;
                        }
                    }
                }
            }

            // Проверка попадания по врагам
            if (enemies != null && !(p instanceof EnemyBullet)) {
                for (Enemy enemy : enemies) {
                    if (!enemy.isDead() &&
                            p.getX() + p.getWidth() > enemy.getX() &&
                            p.getX() < enemy.getX() + enemy.getWidth() &&
                            p.getY() + p.getHeight() > enemy.getY() &&
                            p.getY() < enemy.getY() + enemy.getHeight()) {
                        int damage = p instanceof Laser ? 30 : p instanceof Rocket ? 50 : 10; // Урон от снарядов
                        enemy.takeDamage(damage);
                        iterator.remove();
                        break;
                    }
                }
            }

            // Проверка попадания по игроку
            if (p instanceof EnemyBullet && player != null &&
                    p.getX() + p.getWidth() > player.getX() &&
                    p.getX() < player.getX() + player.getWidth() &&
                    p.getY() + p.getHeight() > player.getY() &&
                    p.getY() < player.getY() + player.getHeight()) {
                player.setHealth(player.getHealth() - 15); // Урон от пули врага
                System.out.println("Игрок получил урон от пули врага. Здоровье: " + player.getHealth());
                iterator.remove();
                if (player.getHealth() <= 0) {
                    game.loseLife("Убит пулей врага");
                }
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (Projectile p : projectiles) {
            p.render(gc);
        }
    }
}