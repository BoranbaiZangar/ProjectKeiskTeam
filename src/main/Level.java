package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Level {

    public static final int TILE_SIZE = 40;
    private List<Tile> tiles = new ArrayList<>();
    private List<Door> doors = new ArrayList<>();
    private List<Button> buttons = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();

    private Image platformImg;
    private Image spikeImg;
    private Image iceImg;
    private Image vanishImg;
    private Image doorImg;
    private Image buttonImg;
    private Image enemyImg;

    private boolean completed = false;

    private int startX = -1, startY = -1;
    private int portalX = -1, portalY = -1;

    public Level(String filename, ProjectileManager projectileManager) {
        try {
            platformImg = new Image(getClass().getResourceAsStream("/images/tileset.png"));
            spikeImg = new Image(getClass().getResourceAsStream("/images/spikes.png"));
            iceImg = new Image(getClass().getResourceAsStream("/images/ice.png"));
            vanishImg = new Image(getClass().getResourceAsStream("/images/platform_vanish.png"));
            doorImg = new Image(getClass().getResourceAsStream("/images/door.png"));
            buttonImg = new Image(getClass().getResourceAsStream("/images/button.png"));
            enemyImg = new Image(getClass().getResourceAsStream("/images/enemy.png"));

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream("/levels/" + filename)));
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
                                buttons.add(new Button(px, py, doors.get(doors.size() - 1), buttonImg));
                            } else {
                                buttons.add(new Button(px, py, null, buttonImg));
                            }
                            break;
                        case 'X':
                            enemies.add(new PatrolEnemy(px, py, enemyImg, projectileManager));
                            break;
                        default:
                            break;
                    }
                }
                y++;
            }

            for (Button button : buttons) {
                if (button.getLinkedDoor() == null && !doors.isEmpty()) {
                    button.setLinkedDoor(doors.get(doors.size() - 1));
                }
            }

            reader.close();
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке уровня: " + filename);
            e.printStackTrace();
        }
    }

    public void advanceToNextLevel() {
        completed = true;
    }

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

    public void update(Player player) {
        for (Button button : buttons) {
            button.update(player);
        }
    }

    public void revealHiddenTiles(Player player) {
        for (Tile tile : tiles) {
            if (tile.isHidden() && Math.abs(tile.getX() - player.getX()) < TILE_SIZE &&
                    Math.abs(tile.getY() - player.getY()) < TILE_SIZE) {
                tile.reveal();
            }
        }
    }

    public List<Door> getDoors() {
        return doors;
    }

    public List<Enemy> getEnemies() {
        return enemies;
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