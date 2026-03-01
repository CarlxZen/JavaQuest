package entities;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Entity {
    protected int x, y;
    protected int width, height;
    protected int speed;

    // Combat stats
    protected int maxHp;
    protected int hp;
    protected int atk;
    protected int def;

    // Invincibility frames
    protected boolean invincible = false;
    protected int invincibleTimer = 0;
    public static final int INVINCIBLE_DURATION = 16; // ~0.5 sec at 33fps

    protected boolean alive = true;

    public Entity(int x, int y, int width, int height, int speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.maxHp = 100;
        this.hp = 100;
        this.atk = 10;
        this.def = 0;
    }

    public abstract void update();
    public abstract void render(Graphics g);

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int takeDamage(int rawDamage) {
        if (invincible || !alive) return 0;
        // +/- 10% variance
        double variance = 0.9 + Math.random() * 0.2;
        int actualDamage = Math.max(1, (int) (rawDamage * variance));
        hp -= actualDamage;
        if (hp <= 0) {
            hp = 0;
            alive = false;
        }
        return actualDamage;
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public void updateInvincibility() {
        if (invincible) {
            invincibleTimer--;
            if (invincibleTimer <= 0) {
                invincible = false;
            }
        }
    }

    public void setInvincible(int duration) {
        invincible = true;
        invincibleTimer = duration;
    }

    public boolean isInvincible() { return invincible; }
    public boolean isAlive() { return alive; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }

    // Getter e Setter
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
}
