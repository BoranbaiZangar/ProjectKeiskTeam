package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class PickupItem extends Item {
    private double x;
    private double y;
    private Image image;
    private boolean pickedUp;

    public PickupItem(String name, double x, double y, Image image) {
        super(name);
        this.x = x;
        this.y = y;
        this.image = image;
        this.pickedUp = false;
    }

    public void render(GraphicsContext gc) {
        if (!pickedUp && image != null) {
            gc.drawImage(image, x, y, 20, 20);
        } else if (!pickedUp) {
            System.err.println("Ошибка: изображение для " + getName() + " не загружено");
        }
    }

    public boolean checkCollision(Player player) {
        if (pickedUp) return false;
        double px = player.getX();
        double py = player.getY();
        double pw = player.getWidth();
        double ph = player.getHeight();

        // Упрощенная проверка столкновений
        boolean collision = px + pw > x && px < x + 20 && py + ph > y && py < y + 20;
        if (collision) {
            System.out.println("Столкновение с предметом: " + getName() + " на (" + x + ", " + y + ")");
        }
        return collision;
    }

    public void pickUp(Player player) {
        if (!pickedUp) {
            player.getInventory().addItem(this);
            pickedUp = true;
            System.out.println("Подобран предмет: " + getName());
        }
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    @Override
    public void use(Player player) {
        // Поведение по умолчанию, переопределяется в подклассах
    }

    public double getX() { return x; }
    public double getY() { return y; }
}