package main;

import javafx.scene.image.Image;

public class AmmoLaser extends PickupItem {
    private int quantity;

    public AmmoLaser(String name, double x, double y, Image image) {
        super(name, x, y, image);
        this.quantity = 5; // Начальное количество лазеров
    }

    @Override
    public void use(Player player) {
        player.setActiveWeapon("laser");
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