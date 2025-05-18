package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Класс, представляющий дверь на уровне.
 * Дверь может быть открыта кнопкой или ключом.
 */
public class Door {
    double x;
    double y;
    private double width = 40, height = 80; // Уменьшены размеры для точного столкновения
    private boolean isOpen;
    private String keyId; // ID ключа, если требуется
    private Image image;

    public Door(double x, double y, String keyId, Image image) {
        this.x = x;
        this.y = y;
        this.keyId = keyId;
        this.image = image;
        this.isOpen = false; // Дверь изначально закрыта
    }

    /**
     * Открывает дверь.
     */
    public void open() {
        isOpen = true;
    }

    /**
     * Проверяет, открыта ли дверь.
     * @return true, если дверь открыта, иначе false
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Проверяет столкновение с игроком.
     * @param player Игрок
     * @param inventory Инвентарь игрока
     * @return true, если столкновение произошло и дверь закрыта
     */
    public boolean checkCollision(Player player, Inventory inventory) {
        if (isOpen || (keyId != null && inventory.hasKey(keyId))) {
            return false; // Дверь открыта или у игрока есть ключ
        }
        double px = player.getX();
        double py = player.getY();
        double pw = player.getWidth();
        double ph = player.getHeight();

        return px + pw > x && px < x + width &&
                py + ph > y && py < y + height;
    }

    /**
     * Отрисовывает дверь, если она не открыта.
     * @param gc Контекст графики
     */
    public void render(GraphicsContext gc) {
        if (!isOpen) {
            gc.drawImage(image, x, y, width, height);
        }
    }
}