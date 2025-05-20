package main;

import javafx.scene.image.Image;

public class AmmoBullet extends PickupItem {
    private int quantity;

    public AmmoBullet(String name, double x, double y, Image image) {
        super(name, x, y, image);
        this.quantity = 10; // Начальное количество пуль
    }

    @Override
    public void use(Player player) {
        player.setActiveWeapon("bullet");
    }

    public int getQuantity() {
        return quantity;
    }

    public void decreaseQuantity() {
        if (quantity > 0) {
            quantity--;
        }
    }
}