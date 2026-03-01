package entities;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Player extends Entity {
    // Direzioni: 0=down, 1=left, 2=right, 3=up
    private BufferedImage[][] idleFrames; // [dir][frame]
    private BufferedImage[][] walkFrames; // [dir][frame]
    private int idleFrameIndex = 0;
    private int walkFrameIndex = 0;
    private boolean walking = false;
    private int walkDir = 0;
    private int animTick = 0;
    private int animSpeed = 8;

    // Attack system
    private boolean attacking = false;
    private int attackTimer = 0;
    private int attackCooldown = 0;
    private static final int ATTACK_DURATION = 8;  // ~240ms
    private static final int ATTACK_COOLDOWN = 15;  // ~450ms
    private boolean attackHitChecked = false;

    public Player(int x, int y) {
        super(x, y, 32, 32, 2);
        this.maxHp = 120;
        this.hp = 120;
        this.atk = 15;
        this.def = 3;
        loadSprites();
    }

    public int getWalkDir() {
        return walkDir;
    }

    public boolean isWalking() {
        return walking;
    }

    private void loadSprites() {
        idleFrames = new BufferedImage[4][2];
        walkFrames = new BufferedImage[4][2];
        String[] dirs = { "down", "left", "right", "up" };
        try {
            for (int d = 0; d < 4; d++) {
                // idle
                InputStream is1 = getClass().getResourceAsStream("/sprites/player/idle_" + dirs[d] + ".png");
                InputStream is2 = getClass().getResourceAsStream("/sprites/player/idle2_" + dirs[d] + ".png");
                idleFrames[d][0] = is1 != null ? ImageIO.read(is1) : null;
                idleFrames[d][1] = is2 != null ? ImageIO.read(is2) : idleFrames[d][0];
                if (is1 != null) is1.close();
                if (is2 != null) is2.close();
                // walk
                InputStream ws1 = getClass().getResourceAsStream("/sprites/player/walk_" + dirs[d] + ".png");
                InputStream ws2 = getClass().getResourceAsStream("/sprites/player/walk2_" + dirs[d] + ".png");
                walkFrames[d][0] = ws1 != null ? ImageIO.read(ws1) : idleFrames[d][0];
                walkFrames[d][1] = ws2 != null ? ImageIO.read(ws2) : walkFrames[d][0];
                if (ws1 != null) ws1.close();
                if (ws2 != null) ws2.close();
            }
        } catch (IOException e) {
            for (int d = 0; d < 4; d++)
                idleFrames[d][0] = idleFrames[d][1] = walkFrames[d][0] = walkFrames[d][1] = null;
        }
    }

    public void attack() {
        if (!attacking && attackCooldown <= 0 && alive) {
            attacking = true;
            attackTimer = ATTACK_DURATION;
            attackHitChecked = false;
            walking = false;
        }
    }

    public boolean isAttacking() { return attacking; }
    public boolean needsHitCheck() { return attacking && !attackHitChecked; }
    public void markHitChecked() { attackHitChecked = true; }

    public Rectangle getAttackHitbox() {
        switch (walkDir) {
            case 0: return new Rectangle(x - 4, y + height, width + 8, 20);
            case 1: return new Rectangle(x - 20, y - 4, 20, height + 8);
            case 2: return new Rectangle(x + width, y - 4, 20, height + 8);
            case 3: return new Rectangle(x - 4, y - 20, width + 8, 20);
            default: return new Rectangle(x, y + height, width, 20);
        }
    }

    public void startWalking(int dx, int dy) {
        if (attacking) return;
        walking = true;
        if (dx == 0 && dy == 1) walkDir = 0;
        else if (dx == -1 && dy == 0) walkDir = 1;
        else if (dx == 1 && dy == 0) walkDir = 2;
        else if (dx == 0 && dy == -1) walkDir = 3;
    }

    public void stopWalking() {
        walking = false;
        walkFrameIndex = 0;
    }

    @Override
    public void update() {
        animTick++;
        updateInvincibility();

        // Attack state
        if (attacking) {
            attackTimer--;
            if (attackTimer <= 0) {
                attacking = false;
                attackCooldown = ATTACK_COOLDOWN;
            }
            return; // Can't move while attacking
        }
        if (attackCooldown > 0) attackCooldown--;

        if (walking) {
            if (animTick % animSpeed == 0) {
                walkFrameIndex = (walkFrameIndex + 1) % walkFrames[walkDir].length;
                switch (walkDir) {
                    case 0: y += speed; break;
                    case 1: x -= speed; break;
                    case 2: x += speed; break;
                    case 3: y -= speed; break;
                }
            }
        } else {
            if (animTick % (animSpeed * 2) == 0) {
                idleFrameIndex = (idleFrameIndex + 1) % idleFrames[walkDir].length;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        // I-frame blinking
        if (invincible && invincibleTimer % 4 < 2) return;

        BufferedImage frame;
        if (walking && walkFrames[walkDir][walkFrameIndex] != null) {
            frame = walkFrames[walkDir][walkFrameIndex];
        } else if (idleFrames[walkDir] != null && idleFrames[walkDir][idleFrameIndex] != null) {
            frame = idleFrames[walkDir][idleFrameIndex];
        } else {
            frame = null;
        }

        if (frame != null) {
            g.drawImage(frame, x, y, width, height, null);
        } else {
            // Fallback: colored square with direction indicator
            g.setColor(new Color(60, 120, 220));
            g.fillRect(x, y, width, height);
            g.setColor(new Color(40, 80, 180));
            g.drawRect(x, y, width - 1, height - 1);
            // Direction indicator
            g.setColor(Color.WHITE);
            int cx = x + width / 2;
            int cy = y + height / 2;
            switch (walkDir) {
                case 0: g.fillRect(cx - 3, y + height - 8, 6, 4); break;
                case 1: g.fillRect(x + 2, cy - 3, 4, 6); break;
                case 2: g.fillRect(x + width - 6, cy - 3, 4, 6); break;
                case 3: g.fillRect(cx - 3, y + 4, 6, 4); break;
            }
        }

        // Attack visual
        if (attacking) {
            Graphics2D g2 = (Graphics2D) g;
            Composite oldComp = g2.getComposite();
            Stroke oldStroke = g2.getStroke();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            Rectangle hitbox = getAttackHitbox();
            g2.setColor(new Color(255, 255, 150));
            g2.fillRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
            // Slash lines
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            int hcx = hitbox.x + hitbox.width / 2;
            int hcy = hitbox.y + hitbox.height / 2;
            g2.drawLine(hcx - 6, hcy - 6, hcx + 6, hcy + 6);
            g2.drawLine(hcx + 6, hcy - 6, hcx - 6, hcy + 6);
            g2.setStroke(oldStroke);
            g2.setComposite(oldComp);
        }
    }
}
