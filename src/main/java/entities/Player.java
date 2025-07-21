package entities;

import java.awt.Graphics;
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
    private int walkDir = 0; // 0=down, 1=left, 2=right, 3=up
    private int animTick = 0;
    private int animSpeed = 8;

    public Player(int x, int y) {
        super(x, y, 32, 32, 2);
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
                if (is1 != null)
                    is1.close();
                if (is2 != null)
                    is2.close();
                // walk
                InputStream ws1 = getClass().getResourceAsStream("/sprites/player/walk_" + dirs[d] + ".png");
                InputStream ws2 = getClass().getResourceAsStream("/sprites/player/walk2_" + dirs[d] + ".png");
                walkFrames[d][0] = ws1 != null ? ImageIO.read(ws1) : idleFrames[d][0];
                walkFrames[d][1] = ws2 != null ? ImageIO.read(ws2) : walkFrames[d][0];
                if (ws1 != null)
                    ws1.close();
                if (ws2 != null)
                    ws2.close();
            }
        } catch (IOException e) {
            for (int d = 0; d < 4; d++)
                idleFrames[d][0] = idleFrames[d][1] = walkFrames[d][0] = walkFrames[d][1] = null;
        }
    }

    public void startWalking(int dx, int dy) {
        walking = true;
        // Determina la direzione
        if (dx == 0 && dy == 1)
            walkDir = 0; // down
        else if (dx == -1 && dy == 0)
            walkDir = 1; // left
        else if (dx == 1 && dy == 0)
            walkDir = 2; // right
        else if (dx == 0 && dy == -1)
            walkDir = 3; // up
        // ... puoi aggiungere diagonali se vuoi
    }

    public void stopWalking() {
        walking = false;
        walkFrameIndex = 0;
    }

    @Override
    public void update() {
        animTick++;
        if (walking) {
            if (animTick % animSpeed == 0) {
                walkFrameIndex = (walkFrameIndex + 1) % walkFrames[walkDir].length;
                switch (walkDir) {
                    case 0:
                        y += speed;
                        break; // down
                    case 1:
                        x -= speed;
                        break; // left
                    case 2:
                        x += speed;
                        break; // right
                    case 3:
                        y -= speed;
                        break; // up
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
        BufferedImage frame;
        if (walking && walkFrames[walkDir][walkFrameIndex] != null) {
            frame = walkFrames[walkDir][walkFrameIndex];
        } else if (idleFrames[walkDir][idleFrameIndex] != null) {
            frame = idleFrames[walkDir][idleFrameIndex];
        } else {
            frame = null;
        }
        if (frame != null) {
            g.drawImage(frame, x, y, width, height, null);
        } else {
            g.setColor(java.awt.Color.WHITE);
            g.fillRect(x, y, 2, 2);
        }
    }
}
