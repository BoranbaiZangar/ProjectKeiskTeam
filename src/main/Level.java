package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Level {

    public static final int TILE_SIZE = 40;
    private List<Tile> tiles = new ArrayList<>();

    private Image platformImg;
    private Image spikeImg;
    private Image iceImg;
    private Image vanishImg;

    private boolean completed = false;

    private int startX = -1, startY = -1;
    private int portalX = -1, portalY = -1;

    public Level(String filename) {
        try {
            platformImg = new Image(getClass().getResourceAsStream("/images/tileset.png"));
            spikeImg = new Image(getClass().getResourceAsStream("/images/spikes.png"));
            iceImg = new Image(getClass().getResourceAsStream("/images/ice.png"));
            vanishImg = new Image(getClass().getResourceAsStream("/images/platform_vanish.png"));

            BufferedReader reader = new BufferedReader(new FileReader("src/resources/levels/" + filename));
            String line;
            int y = 0;

            while ((line = reader.readLine()) != null) {
                for (int x = 0; x < line.length(); x++) {
                    char ch = line.charAt(x);
                    int px = x * TILE_SIZE;
                    int py = y * TILE_SIZE;

                    switch (ch) {
                        case '#':
                            tiles.add(new Tile(px, py, Tile.Type.PLATFORM, platformImg));
                            break;
                        case '^':
                            tiles.add(new Tile(px, py, Tile.Type.SPIKES, spikeImg));
                            break;
                        case '~':
                            tiles.add(new Tile(px, py, Tile.Type.ICE, iceImg));
                            break;
                        case '=':
                            tiles.add(new Tile(px, py, Tile.Type.DISAPPEARING, vanishImg));
                            break;
                        case 'S':
                            startX = px;
                            startY = py;
                            break;
                        case 'P':
                            portalX = px;
                            portalY = py;
                            break;
                        default:
                            break;
                    }
                }
                y++;
            }

            reader.close();
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке уровня: " + filename);
            e.printStackTrace();
        }
    }
    public void advanceToNextLevel() {

        completed = true;
        System.out.println("Переход на следующий уровень!");
        // Тут может быть логика загрузки следующего уровня, например:
        // levelIndex++;  // Увеличиваем индекс уровня
        // loadLevel(levelIndex);  // Загружаем новый уровень
    }

    public void render(GraphicsContext gc) {
        for (Tile tile : tiles) {
            tile.update();
            tile.render(gc);
        }
    }
    public boolean isCompleted() {
        return completed;
    }
    public List<Tile> getTiles() {
        return tiles;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getPortalX() {
        return portalX;
    }

    public int getPortalY() {
        return portalY;
    }
}
