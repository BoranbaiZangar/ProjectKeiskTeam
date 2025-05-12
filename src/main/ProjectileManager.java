package main;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class ProjectileManager {

    private List<Projectile> projectiles;

    public ProjectileManager() {
        projectiles = new ArrayList<>();
    }

    // Метод для добавления нового снаряда
    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    // Обновление всех снарядов
    public void updateProjectiles() {
        List<Projectile> toRemove = new ArrayList<>();
        for (Projectile projectile : projectiles) {
            projectile.update();
            if (projectile.isOutOfBounds(600)) {  // Проверяем, вышел ли снаряд за пределы экрана
                toRemove.add(projectile);
            }
        }
        projectiles.removeAll(toRemove);  // Убираем снаряды, которые вышли за пределы экрана
    }

    // Рендеринг всех снарядов
    public void render(GraphicsContext gc) {
        for (Projectile projectile : projectiles) {
            projectile.render(gc);
        }
    }

    // Получение списка пуль
    public List<Projectile> getProjectiles() {
        return projectiles;
    }
}
