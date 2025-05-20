package main;

import javafx.scene.image.Image;

public class Key extends PickupItem {
    private String doorId;

    public Key(String name, double x, double y, Image image, String doorId) {
        super(name, x, y, image);
        this.doorId = doorId;
    }

    @Override
    public void use(Player player) {
        Level level = player.getLevel();
        for (Door door : level.getDoors()) {
            if (door.getId() != null && door.getId().equals(doorId)) {
                door.open();
                break;
            }
        }
    }

    public String getDoorId() {
        return doorId;
    }
}