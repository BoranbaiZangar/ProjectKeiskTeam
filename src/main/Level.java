package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий уровень игры.
 * Загружает тайлы, двери и другие объекты из текстового файла.
 */
public class Level {

    public static final int TILE_SIZE = 40;
    private List<Tile> tiles = new ArrayList<>();
    private List<Door> doors = new ArrayList<>();
    private List<Button> buttons = new ArrayList<>();

    private Image platformImg;
    private Image spikeImg;
    private Image iceImg;
    private Image vanishImg;
    private Image doorImg;
    private Image buttonImg;

    private boolean completed = false;

    private int startX = -1, startY = -1;
    private int portalX = -1, portalY = -1;

    public Level(String filename) {
        try {
            platformImg = new Image(getClass().getResourceAsStream("/images/tileset.png"));
            spikeImg = new Image(getClass().getResourceAsStream("/images/spikes.png"));
            iceImg = new Image(getClass().getResourceAsStream("/images/ice.png"));
            vanishImg = new Image(getClass().getResourceAsStream("/images/platform_vanish.png"));
            doorImg = new Image(getClass().getResourceAsStream("/images/door.png"));
            buttonImg = new Image(getClass().getResourceAsStream("/images/button.png"));

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
                        case 'D':
                            doors.add(new Door(px, py, null, doorImg));
                            break;
                        case 'H':
                            tiles.add(new Tile(px, py, Tile.Type.PLATFORM, platformImg, true));
                            break;
                        case 'B':
                            if (!doors.isEmpty()) {
                                buttons.add(new Button(px, py, doors.get(doors.size() - 1), buttonImg)); // Связываем с последней дверью
                                System.out.println("Кнопка добавлена на (" + px + ", " + py + ") связана с дверью на (" + doors.get(doors.size() - 1).x + ", " + doors.get(doors.size() - 1).y + ")");
                            } else {
                                buttons.add(new Button(px, py, null, buttonImg)); // Создаем кнопку без двери с ожиданием
                                System.out.println("Кнопка добавлена на (" + px + ", " + py + ") без двери (ожидает связи)");
                            }
                            break;
                        default:
                            break;
                    }
                }
                y++;
            }

            // После загрузки связываем кнопки без дверей с ближайшими дверьми
            for (Button button : buttons) {
                if (button.getLinkedDoor() == null && !doors.isEmpty()) {
                    button.setLinkedDoor(doors.get(doors.size() - 1)); // Связываем с последней дверью
                    System.out.println("Кнопка на (" + button.getX() + ", " + button.getY() + ") связана с дверью на (" + doors.get(doors.size() - 1).x + ", " + doors.get(doors.size() - 1).y + ")");
                }
            }

            reader.close();
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке уровня: " + filename);
            e.printStackTrace();
        }
    }

    /**
     * Переходит на следующий уровень.
     */
    public void advanceToNextLevel() {
        completed = true;
        System.out.println("Переход на следующий уровень!");
    }

    /**
     * Отрисовывает уровень, включая тайлы, двери и кнопки.
     * @param gc Контекст графики
     */
    public void render(GraphicsContext gc) {
        for (Tile tile : tiles) {
            tile.update();
            tile.render(gc);
        }
        for (Door door : doors) {
            door.render(gc);
        }
        for (Button button : buttons) {
            button.render(gc);
        }
    }

    /**
     * Обновляет состояние уровня (например, взаимодействие с кнопками).
     * @param player Игрок
     */
    public void update(Player player) {
        for (Button button : buttons) {
            button.update(player);
        }
    }

    /**
     * Раскрывает скрытые тайлы рядом с игроком.
     * @param player Игрок
     */
    public void revealHiddenTiles(Player player) {
        for (Tile tile : tiles) {
            if (tile.isHidden() && Math.abs(tile.getX() - player.getX()) < TILE_SIZE &&
                    Math.abs(tile.getY() - player.getY()) < TILE_SIZE) {
                tile.reveal();
            }
        }
    }

    /**
     * Возвращает список дверей на уровне.
     * @return Список объектов Door
     */
    public List<Door> getDoors() {
        return doors;
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