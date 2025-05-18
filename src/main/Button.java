package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Класс, представляющий кнопку на уровне.
 * Кнопка открывает связанную дверь при взаимодействии с игроком.
 */
public class Button {
    private double x, y;
    private double width = 15, height = 15;
    private Door linkedDoor;
    private Image image;
    private boolean isPressed;

    public Button(double x, double y, Door linkedDoor, Image image) {
        this.x = x;
        this.y = y;
        this.linkedDoor = linkedDoor;
        this.image = image;
    }

    /**
     * Обновляет состояние кнопки при взаимодействии с игроком.
     * @param player Игрок
     */
    public void update(Player player) {
        isPressed = player.getX() + player.getWidth() > x &&
                player.getX() < x + width &&
                player.getY() + player.getHeight() > y &&
                player.getY() < y + height;
        if (isPressed && linkedDoor != null && !linkedDoor.isOpen()) {
            linkedDoor.open();
        }
    }

    /**
     * Отрисовывает кнопку.
     * @param gc Контекст графики
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }

    // Геттеры и сеттеры
    public double getX() { return x; }
    public double getY() { return y; }
    public Door getLinkedDoor() { return linkedDoor; }
    public void setLinkedDoor(Door linkedDoor) { this.linkedDoor = linkedDoor; }
}