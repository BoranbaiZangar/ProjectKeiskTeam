// ✅ Надёжный генератор проходимых лестничных уровней
package main.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LevelGenerator {

    private static final int WIDTH = 20;
    private static final int HEIGHT = 10;

    public static void generateLevel(String fileName) {
        char[][] grid = new char[HEIGHT][WIDTH];
        for (char[] row : grid) Arrays.fill(row, '.');

        int startX = 1;
        int startY = HEIGHT - 2;
        grid[startY][startX] = 'S';

        Random rand = new Random();
        int steps = 7 + rand.nextInt(4); // количество ступеней
        int x = startX;
        int y = startY;

        // Строим ступенчатый маршрут к порталу
        for (int i = 0; i < steps; i++) {
            // продвигаемся вверх и немного вправо
            if (y > 2) y -= rand.nextInt(2) + 1;
            if (x < WIDTH - 3) x += rand.nextInt(3) + 1;
            grid[y][x] = '#';

            // небольшой шанс поставить лёд или исчезающий блок
            if (rand.nextDouble() < 0.2) grid[y][x] = '~';
            if (rand.nextDouble() < 0.2) grid[y][x] = '=';

            // добавим платформы снизу (удобство прыжка)
            if (y + 1 < HEIGHT && grid[y + 1][x] == '.') {
                grid[y + 1][x] = '#';
            }
        }

        // Помещаем портал в конец лестницы
        grid[y][x] = 'P';

        // Нижняя платформа (пол)
        for (int i = 0; i < WIDTH; i++) grid[HEIGHT - 1][i] = '#';

        // Добавим немного случайных шипов
        for (int i = 0; i < 10; i++) {
            int sx = rand.nextInt(WIDTH);
            int sy = rand.nextInt(HEIGHT - 2);
            if (grid[sy][sx] == '.') grid[sy][sx] = '^';
        }

        try (FileWriter writer = new FileWriter("src/resources/levels/" + fileName)) {
            for (char[] row : grid) {
                writer.write(new String(row));
                writer.write("\n");
            }
            System.out.println("✅ Сгенерирован лестничный уровень: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
