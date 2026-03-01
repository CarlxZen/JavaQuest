package game;

import entities.DamageNumber;
import entities.Enemy;
import entities.Entity;
import entities.Player;
import scene.Scene;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {
    private Scene scene;
    private Player player;
    private List<Enemy> enemies;
    private List<DamageNumber> damageNumbers;
    private int[][] map; // 0=vuoto, 1=ostacolo
    private int tileSize = 32;
    private int gold = 0;

    public Game() {
        this.scene = new Scene();
        this.player = new Player(64, 64);
        this.enemies = new ArrayList<>();
        this.damageNumbers = new ArrayList<>();
        loadMap();
        spawnEnemies();
    }

    private void loadMap() {
        // Esempio: mappa 20x15 con bordo di ostacoli
        int w = 20, h = 15;
        map = new int[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (x == 0 || y == 0 || x == w - 1 || y == h - 1)
                    map[y][x] = 1; // bordo
                else
                    map[y][x] = 0;
            }
        }
        // Aggiungi ostacoli interni
        map[5][5] = 1;
        map[5][6] = 1;
        map[10][10] = 1;
    }

    private void spawnEnemies() {
        enemies.clear();
        // Slime (deboli, lenti)
        enemies.add(new Enemy(200, 200, "Slime", 30, 5, 1, 1,
                new Color(50, 200, 50), 8, 15));
        enemies.add(new Enemy(350, 150, "Slime", 30, 5, 1, 1,
                new Color(80, 180, 80), 8, 15));
        enemies.add(new Enemy(400, 300, "Slime", 30, 5, 1, 1,
                new Color(50, 220, 80), 8, 15));
        // Goblin (medi)
        enemies.add(new Enemy(500, 350, "Goblin", 50, 8, 2, 1,
                new Color(150, 100, 40), 15, 25));
        enemies.add(new Enemy(250, 380, "Goblin", 50, 8, 2, 1,
                new Color(160, 90, 30), 15, 25));
    }

    public void update() {
        if (!player.isAlive()) return;

        // Update enemy AI
        for (Enemy enemy : enemies) {
            enemy.updateAI(player.getX(), player.getY());
        }

        // Player attack vs enemies
        if (player.isAttacking() && player.needsHitCheck()) {
            Rectangle atkBox = player.getAttackHitbox();
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && atkBox.intersects(enemy.getBounds())) {
                    int rawDmg = Math.max(1, player.getAtk() - enemy.getDef());
                    int actualDmg = enemy.takeDamage(rawDmg);
                    if (actualDmg > 0) {
                        damageNumbers.add(new DamageNumber(
                                enemy.getX() + enemy.getWidth() / 2 - 6,
                                enemy.getY() - 10,
                                actualDmg, false, false));
                    }
                    if (!enemy.isAlive()) {
                        gold += enemy.getGoldReward();
                    }
                }
            }
            player.markHitChecked();
        }

        // Enemy attack vs player
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && enemy.canAttack()) {
                Rectangle enemyBounds = enemy.getBounds();
                Rectangle playerBounds = player.getBounds();
                // Expanded attack area around enemy
                Rectangle attackArea = new Rectangle(
                        enemyBounds.x - 6, enemyBounds.y - 6,
                        enemyBounds.width + 12, enemyBounds.height + 12);
                if (attackArea.intersects(playerBounds)) {
                    enemy.performAttack();
                    if (!player.isInvincible()) {
                        int rawDmg = Math.max(1, enemy.getAtk() - player.getDef());
                        int actualDmg = player.takeDamage(rawDmg);
                        if (actualDmg > 0) {
                            player.setInvincible(Entity.INVINCIBLE_DURATION);
                            damageNumbers.add(new DamageNumber(
                                    player.getX() + player.getWidth() / 2 - 6,
                                    player.getY() - 10,
                                    actualDmg, false, false));
                        }
                    }
                }
            }
        }

        // Update damage numbers
        Iterator<DamageNumber> it = damageNumbers.iterator();
        while (it.hasNext()) {
            DamageNumber dn = it.next();
            dn.update();
            if (dn.isExpired()) it.remove();
        }

        // Remove dead enemies
        enemies.removeIf(e -> !e.isAlive());
    }

    public int[][] getMap() { return map; }
    public int getTileSize() { return tileSize; }
    public Scene getScene() { return scene; }
    public Player getPlayer() { return player; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<DamageNumber> getDamageNumbers() { return damageNumbers; }
    public int getGold() { return gold; }

    // Controllo collisione con ostacoli
    public boolean isBlocked(int px, int py) {
        int tx = px / tileSize;
        int ty = py / tileSize;
        if (tx < 0 || ty < 0 || ty >= map.length || tx >= map[0].length)
            return true;
        return map[ty][tx] == 1;
    }
}
