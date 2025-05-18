package main;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Класс, представляющий тайл на уровне игры.
 * Тайлы могут быть платформами, шипами, льдом, исчезающими платформами или пустыми.
 */
public class Tile {

    /**
     * Типы тайлов, определяющие их поведение и внешний вид.
     */
    public enum Type {
        PLATFORM,
        SPIKES,
        ICE,
        DISAPPEARING,
        EMPTY
    }

    private int x, y;
    private Type type;
    private Image image;
    private double size = Level.TILE_SIZE;
    private boolean markedToDisappear = false;
    private long disappearStartTime = 0;
    private double width, height;
    private boolean isHidden; // Поддержка скрытых зон

    /**
     * Конструктор тайла.
     * @param x Координата X
     * @param y Координата Y
     * @param type Тип тайла
     * @param image Изображение тайла
     * @param isHidden Является ли тайл скрытым
     */
    public Tile(int x, int y, Type type, Image image, boolean isHidden) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.image = image;
        this.width = size;
        this.height = size;
        this.isHidden = isHidden;
    }

    public Tile(int x, int y, Type type, Image image) {
        this(x, y, type, image, false); // По умолчанию тайл не скрыт
    }

    /**
     * Отрисовывает тайл на канвасе.
     * @param gc Контекст графики
     */
    public void render(GraphicsContext gc) {
        if (type == Type.EMPTY || isHidden) return;

        if (image != null) {
            if (type == Type.DISAPPEARING && markedToDisappear) {
                long now = System.currentTimeMillis();
                long elapsed = now - disappearStartTime;
                if ((elapsed / 200) % 2 == 0) {
                    gc.drawImage(image, x, y, size, size);
                } else {
                    gc.setFill(Color.DARKRED);
                    gc.fillRect(x, y, size, size);
                }
            } else {
                gc.drawImage(image, x, y, size, size);
            }
        }
    }

    /**
     * Обновляет состояние тайла (например, исчезновение).
     */
    public void update() {
        if (type == Type.DISAPPEARING && markedToDisappear) {
            long now = System.currentTimeMillis();
            if (now - disappearStartTime > 1500) {
                type = Type.EMPTY;
                image = null;
            }
        }
    }

    /**
     * Активирует исчезновение тайла.
     */
    public void triggerDisappear() {
        if (!markedToDisappear) {
            markedToDisappear = true;
            disappearStartTime = System.currentTimeMillis();
        }
    }

    /**
     * Проверяет столкновение с игроком и возвращает, получил ли игрок урон.
     * @param player Игрок
     * @return true, если игрок получил урон (например, от шипов)
     */
    public boolean checkCollision(Player player) {
        if (type == Type.SPIKES && intersects(player)) {
            return true;
        }
        return false;
    }

    /**
     * Проверяет, пересекается ли тайл с игроком.
     * @param player Игрок
     * @return true, если есть пересечение
     */
    public boolean intersects(Player player) {
        double px = player.getX();
        double py = player.getY();
        double pw = player.getWidth();
        double ph = player.getHeight();

        return px < x + size &&
                px + pw > x &&
                py < y + size &&
                py + ph > y;
    }

    /**
     * Возвращает границы тайла для проверки столкновений.
     * @return Объект Bounds
     */
    public Bounds getBounds() {
        return new Rectangle(x, y, width, height).getBoundsInLocal();
    }

    /**
     * Раскрывает скрытый тайл.
     */
    public void reveal() {
        isHidden = false;
    }

    public Type getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getSize() {
        return size;
    }

    public boolean isHidden() {
        return isHidden;
    }

}