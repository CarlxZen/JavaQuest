package entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class DamageNumber {
    private int x;
    private float y;
    private float startY;
    private String text;
    private Color color;
    private int timer;
    private boolean large;
    private static final int DURATION = 26; // ~0.8 sec

    public DamageNumber(int x, int y, int value, boolean critical, boolean miss) {
        this.x = x + (int) (Math.random() * 12 - 6);
        this.startY = y;
        this.y = y;
        this.timer = DURATION;

        if (miss) {
            this.text = "MISS";
            this.color = Color.GRAY;
            this.large = false;
        } else if (critical) {
            this.text = value + "!";
            this.color = new Color(255, 200, 0);
            this.large = true;
        } else {
            this.text = String.valueOf(value);
            this.color = Color.WHITE;
            this.large = false;
        }
    }

    public void update() {
        timer--;
        y = startY - (DURATION - timer) * 0.8f;
    }

    public boolean isExpired() {
        return timer <= 0;
    }

    public void render(Graphics g) {
        if (timer < 3) return;
        g.setFont(new Font("SansSerif", Font.BOLD, large ? 16 : 12));
        g.setColor(color);
        g.drawString(text, x, (int) y);
    }
}
