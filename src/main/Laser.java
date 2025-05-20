package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Laser extends Projectile {
    public Laser(double x, double y, boolean facingRight, Image image) {
        super(x, y, facingRight ? 8 : -8, 0, image);
        this.width = 90;
        this.height = 20;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image == null) {
            System.err.println("Ошибка: изображение лазера не загружено!");
            gc.setFill(Color.RED); // Отрисовка красного прямоугольника для отладки
            gc.fillRect(x, y, width, height);
        } else {
            gc.drawImage(image, x, y, width, height);
        }
    }}