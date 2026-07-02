package ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public final class MenuRenderer {

    private static final Color BG_TOP = new Color(15, 18, 35);
    private static final Color BG_BOTTOM = new Color(28, 35, 65);
    private static final Color ACCENT = new Color(0, 210, 255);
    private static final Color ACCENT_WARM = new Color(255, 180, 70);
    private static final Color TEXT_PRIMARY = new Color(235, 240, 255);
    private static final Color TEXT_MUTED = new Color(120, 130, 160);
    private static final Color CARD_FILL = new Color(255, 255, 255, 18);
    private static final Color CARD_SELECTED = new Color(0, 210, 255, 38);

    private MenuRenderer() {
    }

    public static void renderTitleScreen(Graphics2D g, int width, int height, String[] options,
            int selectedOption, float animPhase) {
        setupQuality(g);
        drawBackground(g, width, height, animPhase);
        drawDecorativeOrbs(g, width, height, animPhase);
        drawTitle(g, width, "JavaQuest", "Un'avventura in Java");
        drawMenuButtons(g, width, height, options, selectedOption, animPhase);
        drawFooterHint(g, width, height, "\u2191 \u2193  Naviga     \u23CE  Conferma");
    }

    public static void renderOptionsScreen(Graphics2D g, int width, int height, float animPhase) {
        setupQuality(g);
        drawBackground(g, width, height, animPhase);
        drawDecorativeOrbs(g, width, height, animPhase);
        drawTitle(g, width, "Opzioni", "Personalizza la tua esperienza");

        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.setColor(TEXT_MUTED);
        String msg = "Nessuna opzione disponibile.";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, (width - fm.stringWidth(msg)) / 2, height / 2);

        drawFooterHint(g, width, height, "ESC  Torna al menu");
    }

    private static void setupQuality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private static void drawBackground(Graphics2D g, int width, int height, float animPhase) {
        GradientPaint gradient = new GradientPaint(0, 0, BG_TOP, 0, height, BG_BOTTOM);
        g.setPaint(gradient);
        g.fillRect(0, 0, width, height);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
        g.setColor(new Color(255, 255, 255, 12));
        int gridSize = 40;
        for (int x = 0; x < width; x += gridSize) {
            g.drawLine(x, 0, x, height);
        }
        for (int y = 0; y < height; y += gridSize) {
            g.drawLine(0, y, width, y);
        }
        g.setComposite(AlphaComposite.SrcOver);

        drawParticles(g, width, height, animPhase);
    }

    private static void drawParticles(Graphics2D g, int width, int height, float animPhase) {
        float[][] particles = {
                { 0.12f, 0.18f, 2.5f }, { 0.28f, 0.72f, 1.8f }, { 0.45f, 0.35f, 2.2f },
                { 0.62f, 0.15f, 1.5f }, { 0.78f, 0.55f, 2.8f }, { 0.88f, 0.82f, 1.6f },
                { 0.35f, 0.88f, 2.0f }, { 0.55f, 0.65f, 1.4f }, { 0.92f, 0.28f, 2.3f }
        };
        for (float[] p : particles) {
            float pulse = (float) (0.4 + 0.6 * Math.sin(animPhase * 2 + p[0] * 10));
            int px = (int) (p[0] * width);
            int py = (int) ((p[1] + Math.sin(animPhase + p[2]) * 0.02) * height);
            float size = p[2] * pulse;
            float alpha = Math.max(0f, Math.min(1f, 0.25f * pulse));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setColor(ACCENT);
            g.fill(new Ellipse2D.Float(px, py, size, size));
        }
        g.setComposite(AlphaComposite.SrcOver);
    }

    private static void drawDecorativeOrbs(Graphics2D g, int width, int height, float animPhase) {
        float drift = (float) Math.sin(animPhase * 0.8) * 15;
        RadialGradientPaint orb1 = new RadialGradientPaint(
                width * 0.15f + drift, height * 0.25f, 120,
                new float[] { 0f, 1f },
                new Color[] { new Color(0, 210, 255, 45), new Color(0, 210, 255, 0) });
        g.setPaint(orb1);
        g.fillRect(0, 0, width, height);

        RadialGradientPaint orb2 = new RadialGradientPaint(
                width * 0.85f - drift, height * 0.7f, 100,
                new float[] { 0f, 1f },
                new Color[] { new Color(255, 120, 80, 35), new Color(255, 120, 80, 0) });
        g.setPaint(orb2);
        g.fillRect(0, 0, width, height);
    }

    private static void drawTitle(Graphics2D g, int width, String title, String subtitle) {
        int centerX = width / 2;
        int titleY = 115;

        g.setFont(new Font("SansSerif", Font.BOLD, 54));
        FontMetrics fmTitle = g.getFontMetrics();
        int titleWidth = fmTitle.stringWidth(title);
        int titleX = centerX - titleWidth / 2;

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g.setColor(ACCENT);
        g.drawString(title, titleX + 2, titleY + 2);
        g.setComposite(AlphaComposite.SrcOver);

        LinearGradientPaint titleGradient = new LinearGradientPaint(
                titleX, titleY - 40, titleX + titleWidth, titleY,
                new float[] { 0f, 0.5f, 1f },
                new Color[] { ACCENT, TEXT_PRIMARY, ACCENT_WARM });
        g.setPaint(titleGradient);
        g.drawString(title, titleX, titleY);

        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g.setColor(TEXT_MUTED);
        FontMetrics fmSub = g.getFontMetrics();
        g.drawString(subtitle, centerX - fmSub.stringWidth(subtitle) / 2, titleY + 32);

        int lineW = 180;
        int lineY = titleY + 48;
        GradientPaint line = new GradientPaint(centerX - lineW / 2f, lineY, new Color(0, 0, 0, 0),
                centerX, lineY, ACCENT);
        g.setPaint(line);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(centerX - lineW / 2, lineY, centerX + lineW / 2, lineY);
    }

    private static void drawMenuButtons(Graphics2D g, int width, int height, String[] options,
            int selectedOption, float animPhase) {
        int buttonW = 300;
        int buttonH = 48;
        int spacing = 14;
        int totalH = options.length * buttonH + (options.length - 1) * spacing;
        int startY = (height - totalH) / 2 + 30;
        int buttonX = (width - buttonW) / 2;

        float pulse = (float) (0.7 + 0.3 * Math.sin(animPhase * 3));

        for (int i = 0; i < options.length; i++) {
            boolean isDisabled = (i == 1);
            boolean isSelected = (i == selectedOption);
            int y = startY + i * (buttonH + spacing);

            RoundRectangle2D card = new RoundRectangle2D.Float(buttonX, y, buttonW, buttonH, 14, 14);

            if (isSelected) {
                float glowAlpha = Math.max(0f, Math.min(1f, 0.25f * pulse));
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glowAlpha));
                g.setColor(ACCENT);
                g.fill(new RoundRectangle2D.Float(buttonX - 4, y - 4, buttonW + 8, buttonH + 8, 18, 18));
                g.setComposite(AlphaComposite.SrcOver);
            }

            g.setColor(isSelected ? CARD_SELECTED : CARD_FILL);
            g.fill(card);

            if (isSelected) {
                g.setStroke(new BasicStroke(2f));
                g.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(),
                        (int) (180 + 75 * pulse)));
                g.draw(card);
            } else if (!isDisabled) {
                g.setStroke(new BasicStroke(1f));
                g.setColor(new Color(255, 255, 255, 30));
                g.draw(card);
            }

            Font font = new Font("SansSerif", isSelected ? Font.BOLD : Font.PLAIN, 20);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            String text = options[i];
            int textX = buttonX + (buttonW - fm.stringWidth(text)) / 2;
            int textY = y + (buttonH + fm.getAscent() - fm.getDescent()) / 2;

            if (isDisabled) {
                g.setColor(new Color(80, 85, 100));
                g.drawString(text, textX, textY);
                g.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g.setColor(new Color(100, 105, 120));
                String badge = "PROSSIMAMENTE";
                FontMetrics fmBadge = g.getFontMetrics();
                g.drawString(badge, buttonX + buttonW - fmBadge.stringWidth(badge) - 14,
                        y + buttonH / 2 + fmBadge.getAscent() / 2 - 2);
            } else if (isSelected) {
                g.setColor(ACCENT);
                g.drawString(text, textX, textY);
            } else {
                g.setColor(TEXT_PRIMARY);
                g.drawString(text, textX, textY);
            }
        }
    }

    private static void drawFooterHint(Graphics2D g, int width, int height, String hint) {
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        FontMetrics fm = g.getFontMetrics();
        int hintW = fm.stringWidth(hint) + 32;
        int hintH = 30;
        int hintX = (width - hintW) / 2;
        int hintY = height - 55;

        RoundRectangle2D pill = new RoundRectangle2D.Float(hintX, hintY, hintW, hintH, 15, 15);
        g.setColor(new Color(255, 255, 255, 12));
        g.fill(pill);
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(255, 255, 255, 25));
        g.draw(pill);

        g.setColor(TEXT_MUTED);
        g.drawString(hint, hintX + 16, hintY + hintH / 2 + fm.getAscent() / 2 - 2);
    }
}
