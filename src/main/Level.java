package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Level {
    private List<String> lines = new ArrayList<>();
    public static final int TILE_SIZE = 40;

    public Level(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/levels/" + filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void render(GraphicsContext gc) {
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char tile = line.charAt(x);
                switch (tile) {
                    case '#':
                        gc.setFill(Color.GRAY);
                        gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        break;
                    case '_':
                        gc.setFill(Color.DARKGREEN);
                        gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        break;
                    case 'P':
                        gc.setFill(Color.YELLOW);
                        gc.fillOval(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        break;
                    default:
                        // Пустые клетки можно пропускать
                        break;
                }
            }
        }
    }

    public List<String> getLines() {
        return lines;
    }
}
