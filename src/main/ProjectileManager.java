package main;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProjectileManager {

    private final List<Projectile> projectiles = new ArrayList<>();

    // 🔹 Ссылка на тайлы (платформы, стены)
    private List<Tile> tiles;

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public void addProjectile(Projectile p) {
        projectiles.add(p);
    }

    public void updateProjectiles() {
        Iterator<Projectile> iterator = projectiles.iterator();

        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            p.update();

            // Удалить, если за пределами экрана
            if (p.getX() < 0 || p.getX() > Main.WIDTH) {
                iterator.remove();
                continue;
            }

            // 🔥 Проверка на столкновение с платформой
            if (tiles != null) {
                for (Tile tile : tiles) {
                    if (tile.getType() == Tile.Type.PLATFORM || tile.getType() == Tile.Type.ICE) {
                        if (tile.getBounds().intersects(p.getBounds())) {
                            iterator.remove(); // удаляем пулю при столкновении
                            break;
                        }
                    }
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
