package main;

import javafx.scene.image.Image;

public class AmmoRocket extends PickupItem {
    private int quantity;

    public AmmoRocket(String name, double x, double y, Image image) {
        super(name, x, y, image);
        this.quantity = 3; // Начальное количество ракет
    }

    @Override
    public void use(Player player) {
        player.setActiveWeapon("rocket");
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