package main;

import javafx.scene.image.Image;

public class Key extends PickupItem {
    private int scoreValue; // Количество очков за подбор ключа

    public Key(String name, double x, double y, Image image, int scoreValue) {
        super(name, x, y, image);
        this.scoreValue = scoreValue;
    }

    @Override
    public void pickUp(Player player) {
        if (!isPickedUp()) {
            player.addScore(scoreValue); // Добавляем очки игроку
            setPickedUp(true); // Помечаем ключ как подобранный
            System.out.println("Подобран ключ: " + getName() + ", добавлено " + scoreValue + " очков");
        }
    }

    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }
}