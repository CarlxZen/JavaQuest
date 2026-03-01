package entities;

import java.awt.Color;
import java.awt.Graphics;

public class Enemy extends Entity {
    public enum AIState { IDLE, CHASE, ATTACK, RETURN }

    private AIState aiState = AIState.IDLE;
    private int spawnX, spawnY;
    private int detectionRange;
    private int attackRange;
    private int attackCooldown = 0;
    private int attackCooldownMax;
    private int moveTick = 0;
    private int moveRate;
    private String name;
    private Color color;
    private int goldReward;
    private int expReward;
    private int damageFlash = 0;

    public Enemy(int x, int y, String name, int hp, int atk, int def, int speed,
                 Color color, int goldReward, int expReward) {
        super(x, y, 28, 28, speed);
        this.spawnX = x;
        this.spawnY = y;
        this.name = name;
        this.maxHp = hp;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.color = color;
        this.goldReward = goldReward;
        this.expReward = expReward;
        this.detectionRange = 160;
        this.attackRange = 36;
        this.attackCooldownMax = 60;
        this.moveRate = 5;
    }

    public void updateAI(int playerX, int playerY) {
        if (!alive) return;

        if (damageFlash > 0) damageFlash--;
        if (attackCooldown > 0) attackCooldown--;

        moveTick++;
        boolean canMove = (moveTick % moveRate == 0);

        double dx = playerX - x;
        double dy = playerY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        switch (aiState) {
            case IDLE:
                if (canMove && moveTick % 30 == 0) {
                    int dir = (int) (Math.random() * 5);
                    if (dir < 4) {
                        int mx = dir == 2 ? speed : dir == 1 ? -speed : 0;
                        int my = dir == 0 ? speed : dir == 3 ? -speed : 0;
                        if (Math.abs((x + mx) - spawnX) < 64 && Math.abs((y + my) - spawnY) < 64) {
                            x += mx;
                            y += my;
                        }
                    }
                }
                if (dist < detectionRange) {
                    aiState = AIState.CHASE;
                }
                break;

            case CHASE:
                if (canMove) {
                    if (dx < -2) x -= speed;
                    else if (dx > 2) x += speed;
                    if (dy < -2) y -= speed;
                    else if (dy > 2) y += speed;
                }
                if (dist <= attackRange) {
                    aiState = AIState.ATTACK;
                } else if (dist > detectionRange * 2) {
                    aiState = AIState.RETURN;
                }
                break;

            case ATTACK:
                if (dist > attackRange * 2) {
                    aiState = AIState.CHASE;
                }
                break;

            case RETURN:
                if (canMove) {
                    if (spawnX < x) x -= speed;
                    else if (spawnX > x) x += speed;
                    if (spawnY < y) y -= speed;
                    else if (spawnY > y) y += speed;
                }
                if (Math.abs(x - spawnX) < 4 && Math.abs(y - spawnY) < 4) {
                    x = spawnX;
                    y = spawnY;
                    aiState = AIState.IDLE;
                    hp = maxHp;
                }
                if (dist < detectionRange) {
                    aiState = AIState.CHASE;
                }
                break;
        }
    }

    public boolean canAttack() {
        return aiState == AIState.ATTACK && attackCooldown <= 0 && alive;
    }

    public void performAttack() {
        attackCooldown = attackCooldownMax;
    }

    @Override
    public int takeDamage(int rawDamage) {
        int dmg = super.takeDamage(rawDamage);
        if (dmg > 0) damageFlash = 6;
        return dmg;
    }

    @Override
    public void update() {
        // AI update is handled via updateAI()
    }

    @Override
    public void render(Graphics g) {
        if (!alive) return;

        int drawX = x + 2;
        int drawY = y + 2;

        // Body
        g.setColor(damageFlash > 0 ? Color.WHITE : color);
        g.fillRect(drawX, drawY, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(drawX, drawY, width, height);

        // Eyes
        g.setColor(Color.WHITE);
        g.fillOval(drawX + 6, drawY + 8, 6, 6);
        g.fillOval(drawX + width - 12, drawY + 8, 6, 6);
        g.setColor(Color.BLACK);
        g.fillOval(drawX + 8, drawY + 10, 3, 3);
        g.fillOval(drawX + width - 10, drawY + 10, 3, 3);

        // HP bar above enemy
        int barWidth = 30;
        int barHeight = 4;
        int barX = x + (width + 4 - barWidth) / 2;
        int barY = y - 6;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);
        float hpPct = (float) hp / maxHp;
        g.setColor(hpPct > 0.5f ? new Color(50, 200, 50) :
                   hpPct > 0.25f ? Color.YELLOW : Color.RED);
        g.fillRect(barX, barY, (int) (barWidth * hpPct), barHeight);
    }

    public AIState getAIState() { return aiState; }
    public String getName() { return name; }
    public int getGoldReward() { return goldReward; }
    public int getExpReward() { return expReward; }
}
