package main;

public class Key extends Item {
    private String doorId;

    public Key(String doorId) {
        super("Key");
        this.doorId = doorId;
    }

    public String getDoorId() {
        return doorId;
    }

    @Override
    public void use(Player player) {
        for (Door door : player.getLevel().getDoors()) {
            if (door.getKeyId() != null && door.getKeyId().equals(doorId) && !door.isOpen()) {
                door.open();
                break;
            }
        }
    }
}