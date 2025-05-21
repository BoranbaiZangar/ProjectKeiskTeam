package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Set;

public class Player {
    private double x;
    private double y;
    private final double width = 30;
    private final double height = 35;
    private int health = 100;
    private int maxHealth = 100;
    private int score = 0;

    private double velocityY = 0;
    private boolean canJump = false;
    private boolean canShoot = true;
    private double shootCooldown = 0;
    private static final double SHOOT_DELAY = 0.5;

    private final double MOVE_SPEED = 2.5;
    private final double JUMP_FORCE = -12;
    private final double GRAVITY = 0.5;

    private final double GROUND_Y = Main.HEIGHT - height;
    private List<Tile> tiles;
    private Level level;

    private Image playerImageRight;
    private Image playerImageLeft;
    private Image currentPlayerImage;

    private AudioClip jumpSound;
    private boolean isJumpSoundMuted = false;
    private Image bulletImage;
    private Image laserImage;
    private Image rocketImage;

    private ProjectileManager projectileManager;
    private double velocityX = 0;
    private boolean onIce = false;
    private boolean facingRight = true;
    private Inventory inventory;
    private String activeWeapon = null;

    public Player(double startX, double startY, Level level,
                  Image bulletImage, Image laserImage, Image rocketImage,
                  Image playerImageLeft, Image playerImageRight, Inventory inventory,
                  ProjectileManager projectileManager) {
        this.x = startX;
        this.y = startY;
        this.level = level;
        this.tiles = level.getTiles();
        this.playerImageRight = playerImageRight;
        this.playerImageLeft = playerImageLeft;
        this.currentPlayerImage = playerImageRight;
        this.bulletImage = bulletImage;
        this.laserImage = laserImage;
        this.rocketImage = rocketImage;

        this.projectileManager = projectileManager;

        this.jumpSound = new AudioClip(getClass().getResource("/sounds/jump.wav").toString());
        this.projectileManager.setTiles(this.tiles);
        this.inventory = inventory != null ? inventory : new Inventory();
    }

    public void update(Set<KeyCode> keysPressed) {
        if (keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.A)) {
            velocityX = -MOVE_SPEED;
            facingRight = false;
            currentPlayerImage = playerImageLeft;
        } else if (keysPressed.contains(KeyCode.RIGHT) || keysPressed.contains(KeyCode.D)) {
            velocityX = MOVE_SPEED;
            facingRight = true;
            currentPlayerImage = playerImageRight;
        } else if (!onIce) {
            velocityX = 0;
        } else {
            velocityX *= 0.95;
            if (Math.abs(velocityX) < 0.1) velocityX = 0;
        }

        projectileManager.updateProjectiles();

        if ((keysPressed.contains(KeyCode.UP) || keysPressed.contains(KeyCode.W) || keysPressed.contains(KeyCode.SPACE)) && canJump) {
            velocityY = JUMP_FORCE;
            canJump = false;
            jumpSound.play();
        }

        if (shootCooldown > 0) {
            shootCooldown -= 1.0 / 60;
            if (shootCooldown <= 0) {
                canShoot = true;
            }
        }

        velocityY += GRAVITY;

        onIce = false;

        for (Tile tile : tiles) {
            double tx = tile.getX();
            double ty = tile.getY();
            double footX = x + width / 2;
            double footY = y + height + 1;

            boolean standingOnTile = footX > tx && footX < tx + Level.TILE_SIZE &&
                    footY > ty && footY < ty + Level.TILE_SIZE;

            if (standingOnTile && tile.getType() == Tile.Type.ICE) {
                onIce = true;
            }

            if (tile.getType() == Tile.Type.DISAPPEARING) {
                double dx = tile.getX();
                double dy = tile.getY();
                double footX2 = x + width / 2;
                double footY2 = y + height + 1;

                boolean onTop = footX2 > dx && footX2 < dx + Level.TILE_SIZE &&
                        footY2 > dy && footY2 < dy + Level.TILE_SIZE;

                if (onTop) {
                    tile.triggerDisappear();
                }
            }
        }

        double nextX = x + velocityX;
        double nextY = y + velocityY;

        if (!isColliding(nextX, y)) x = nextX;
        if (!isColliding(x, nextY)) {
            y = nextY;
        } else {
            if (velocityY > 0) canJump = true;
            velocityY = 0;
        }

        if (y + height >= GROUND_Y) {
            y = GROUND_Y - height;
            velocityY = 0;
            canJump = true;
        }

        if (x < 0) x = 0;
        if (x + width > Main.WIDTH) x = Main.WIDTH - width;

        if (y < 0) y = 0;
        if (y + height > Main.HEIGHT) y = Main.HEIGHT - height;
    }

    public void shoot() {
        if (!canShoot || activeWeapon == null) return;

        double offsetX = facingRight ? width : -20;
        PickupItem activeAmmo = null;

        for (Item item : inventory.getItems()) {
            if ((item instanceof AmmoBullet && activeWeapon.equals("bullet")) ||
                    (item instanceof AmmoRocket && activeWeapon.equals("rocket")) ||
                    (item instanceof AmmoLaser && activeWeapon.equals("laser"))) {
                activeAmmo = (PickupItem) item;
                break;
            }
        }

        if (activeAmmo == null ||
                (activeAmmo instanceof AmmoBullet && ((AmmoBullet)activeAmmo).getQuantity() <= 0) ||
                (activeAmmo instanceof AmmoRocket && ((AmmoRocket)activeAmmo).getQuantity() <= 0) ||
                (activeAmmo instanceof AmmoLaser && ((AmmoLaser)activeAmmo).getQuantity() <= 0)) {
            activeWeapon = null;
            return;
        }

        switch (activeWeapon) {
            case "bullet":
                projectileManager.addProjectile(new Bullet(x + offsetX, y + height / 2, facingRight, bulletImage));
                ((AmmoBullet)activeAmmo).decreaseQuantity();
                System.out.println("Выстрел пулей, осталось: " + ((AmmoBullet)activeAmmo).getQuantity());
                break;
            case "rocket":
                projectileManager.addProjectile(new Rocket(x + (facingRight ? width : -14), y + height / 2, facingRight, rocketImage));
                ((AmmoRocket)activeAmmo).decreaseQuantity();
                System.out.println("Выстрел ракетой, осталось: " + ((AmmoRocket)activeAmmo).getQuantity());
                break;
            case "laser":
                projectileManager.addProjectile(new Laser(x + (facingRight ? width : -90), y + height / 2, facingRight, laserImage));
                ((AmmoLaser)activeAmmo).decreaseQuantity();
                System.out.println("Выстрел лазером, осталось: " + ((AmmoLaser)activeAmmo).getQuantity());
                break;
        }

        canShoot = false;
        shootCooldown = SHOOT_DELAY;

        if ((activeAmmo instanceof AmmoBullet && ((AmmoBullet)activeAmmo).getQuantity() == 0) ||
                (activeAmmo instanceof AmmoRocket && ((AmmoRocket)activeAmmo).getQuantity() == 0) ||
                (activeAmmo instanceof AmmoLaser && ((AmmoLaser)activeAmmo).getQuantity() == 0)) {
            inventory.getItems().remove(activeAmmo);
            activeWeapon = null;
            System.out.println("Боеприпасы " + activeWeapon + " закончились");
        }
    }

    // Устаревший метод, сохранён для совместимости
    @Deprecated
    public void shootRocket() {
        // Метод не используется, так как стрельба реализована через shoot()
    }

    // Устаревший метод, сохранён для совместимости
    @Deprecated
    public void shootLaser() {
        // Метод не используется, так как стрельба реализована через shoot()
    }

    public void render(GraphicsContext gc) {
        if (currentPlayerImage == null) {
            gc.setFill(Color.RED);
            gc.fillRect(x, y, width, height);
        } else {
            gc.drawImage(currentPlayerImage, x, y, width, height);
        }
        projectileManager.render(gc);
    }

    private boolean isColliding(double nextX, double nextY) {
        for (Tile tile : tiles) {
            Tile.Type type = tile.getType();
            if (type == Tile.Type.PLATFORM || type == Tile.Type.ICE || type == Tile.Type.DISAPPEARING) {
                double tx = tile.getX();
                double ty = tile.getY();

                boolean overlap = nextX + width > tx &&
                        nextX < tx + Level.TILE_SIZE &&
                        nextY + height > ty &&
                        nextY < ty + Level.TILE_SIZE;

                if (overlap) return true;
            }
        }

        if (level != null && level.getDoors() != null) {
            for (Door door : level.getDoors()) {
                if (door.checkCollision(this)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setJumpSoundMuted(boolean muted) {
        this.isJumpSoundMuted = muted;
        if (jumpSound != null) {
            jumpSound.setVolume(muted ? 0 : 1.0);
        }
    }

    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }

    public void setHealth(int newHealth) {
        this.health = Math.min(maxHealth, Math.max(0, newHealth));
        System.out.println("Установлено здоровье: " + health);
    }

    public void addScore(int points) {
        score += points;
        System.out.println("Очки игрока: " + score);
    }

    public void setScore(int newScore) {
        this.score = Math.max(0, newScore);
        System.out.println("Установлены очки: " + score);
    }

    public int getScore() {
        return score;
    }

    public void setActiveWeapon(String weapon) {
        this.activeWeapon = weapon;
    }

    public String getActiveWeapon() {
        return activeWeapon;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public Inventory getInventory() { return inventory; }
    public Level getLevel() { return level; }
    public int getHealth() { return health; }
}