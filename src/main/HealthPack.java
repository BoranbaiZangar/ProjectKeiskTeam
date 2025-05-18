package main;

public class HealthPack extends Item {
    private int healAmount;

    public HealthPack(int healAmount) {
        super("HealthPack");
        this.healAmount = healAmount;
    }

    @Override
    public void use(Player player) {
        // Требуется метод для восстановления здоровья в Player
    }
}