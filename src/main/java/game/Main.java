package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import entities.Player;
import rendering.Render;
import utils.Logger;

public class Main extends JPanel {
    private Game game;
    private Player player;
    private Render render;
    private int width = 640, height = 480;

    private GameState gameState = GameState.TITLE_SCREEN;
    private int selectedOption = 0;
    private static final String[] TITLE_OPTIONS = { "Nuova Partita", "Continua Partita", "Opzioni" };

    public Main() {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        render = new Render();
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (gameState) {
                    case TITLE_SCREEN:
                        handleTitleInput(key);
                        break;
                    case PLAYING:
                        handleGameInput(key);
                        break;
                    case OPTIONS:
                        handleOptionsInput(key);
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (gameState == GameState.PLAYING) {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_UP
                            || key == KeyEvent.VK_DOWN) {
                        player.stopWalking();
                    }
                }
            }
        });
        // Timer per aggiornare animazione e movimento
        new javax.swing.Timer(30, evt -> {
            if (gameState == GameState.PLAYING && player != null) {
                // Aggiorna posizione solo se non c'è ostacolo
                int nextX = player.getX();
                int nextY = player.getY();
                if (playerIsMoving()) {
                    int dx = 0, dy = 0;
                    switch (player.getWalkDir()) {
                        case 0:
                            dy = 1;
                            break;
                        case 1:
                            dx = -1;
                            break;
                        case 2:
                            dx = 1;
                            break;
                        case 3:
                            dy = -1;
                            break;
                    }
                    nextX += dx * player.getSpeed();
                    nextY += dy * player.getSpeed();
                    if (!game.isBlocked(nextX, nextY)) {
                        player.update();
                    } else {
                        player.stopWalking();
                    }
                } else {
                    player.update();
                }
            }
            repaint();
        }).start();
    }

    private void handleTitleInput(int key) {
        if (key == KeyEvent.VK_UP) {
            do {
                selectedOption = (selectedOption - 1 + TITLE_OPTIONS.length) % TITLE_OPTIONS.length;
            } while (selectedOption == 1); // Salta "Continua Partita"
        } else if (key == KeyEvent.VK_DOWN) {
            do {
                selectedOption = (selectedOption + 1) % TITLE_OPTIONS.length;
            } while (selectedOption == 1); // Salta "Continua Partita"
        } else if (key == KeyEvent.VK_ENTER) {
            switch (selectedOption) {
                case 0: // Nuova Partita
                    startNewGame();
                    break;
                case 1: // Continua Partita - disabilitato
                    break;
                case 2: // Opzioni
                    gameState = GameState.OPTIONS;
                    break;
            }
        }
    }

    private void handleGameInput(int key) {
        int dx = 0, dy = 0;
        if (key == KeyEvent.VK_LEFT)
            dx = -1;
        if (key == KeyEvent.VK_RIGHT)
            dx = 1;
        if (key == KeyEvent.VK_UP)
            dy = -1;
        if (key == KeyEvent.VK_DOWN)
            dy = 1;
        // Simula movimento solo se non c'è ostacolo
        int nextX = player.getX() + dx * player.getSpeed();
        int nextY = player.getY() + dy * player.getSpeed();
        if (!game.isBlocked(nextX, nextY)) {
            player.startWalking(dx, dy);
        }
    }

    private void handleOptionsInput(int key) {
        if (key == KeyEvent.VK_ESCAPE) {
            gameState = GameState.TITLE_SCREEN;
        }
    }

    private void startNewGame() {
        game = new Game();
        player = game.getPlayer();
        gameState = GameState.PLAYING;
    }

    private boolean playerIsMoving() {
        // Ritorna true se il player sta camminando
        return player != null && player.isWalking();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        switch (gameState) {
            case TITLE_SCREEN:
                paintTitleScreen(g2);
                break;
            case PLAYING:
                paintGame(g2);
                break;
            case OPTIONS:
                paintOptions(g2);
                break;
        }
    }

    private void paintTitleScreen(Graphics2D g) {
        // Sfondo
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // Titolo
        g.setFont(new Font("Serif", Font.BOLD, 52));
        g.setColor(new Color(200, 170, 50));
        String title = "JavaQuest";
        FontMetrics fmTitle = g.getFontMetrics();
        int titleX = (width - fmTitle.stringWidth(title)) / 2;
        g.drawString(title, titleX, 140);

        // Opzioni del menu
        Font menuFont = new Font("SansSerif", Font.PLAIN, 24);
        Font menuFontSelected = new Font("SansSerif", Font.BOLD, 26);
        int startY = 250;
        int spacing = 50;

        for (int i = 0; i < TITLE_OPTIONS.length; i++) {
            boolean isDisabled = (i == 1); // "Continua Partita" disabilitato
            boolean isSelected = (i == selectedOption);

            if (isSelected) {
                g.setFont(menuFontSelected);
                g.setColor(new Color(255, 220, 80));
            } else if (isDisabled) {
                g.setFont(menuFont);
                g.setColor(new Color(100, 100, 100));
            } else {
                g.setFont(menuFont);
                g.setColor(Color.WHITE);
            }

            FontMetrics fm = g.getFontMetrics();
            String text = TITLE_OPTIONS[i];
            int textX = (width - fm.stringWidth(text)) / 2;
            int textY = startY + i * spacing;
            g.drawString(text, textX, textY);

            // Freccia indicatore per l'opzione selezionata
            if (isSelected) {
                String arrow = "\u25B6";
                int arrowX = textX - fm.stringWidth(arrow) - 10;
                g.drawString(arrow, arrowX, textY);
            }
        }

        // Istruzioni in basso
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(new Color(150, 150, 150));
        String hint = "Usa \u2191\u2193 per navigare, INVIO per confermare";
        FontMetrics fmHint = g.getFontMetrics();
        int hintX = (width - fmHint.stringWidth(hint)) / 2;
        g.drawString(hint, hintX, height - 40);
    }

    private void paintGame(Graphics2D g) {
        // Renderizza la mappa
        int[][] map = game.getMap();
        int tileSize = game.getTileSize();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 1) {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                }
            }
        }
        // Renderizza il player
        player.render(g);
    }

    private void paintOptions(Graphics2D g) {
        // Sfondo
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // Titolo
        g.setFont(new Font("Serif", Font.BOLD, 40));
        g.setColor(new Color(200, 170, 50));
        String title = "Opzioni";
        FontMetrics fmTitle = g.getFontMetrics();
        int titleX = (width - fmTitle.stringWidth(title)) / 2;
        g.drawString(title, titleX, 140);

        // Messaggio vuoto
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.setColor(new Color(180, 180, 180));
        String msg = "Nessuna opzione disponibile.";
        FontMetrics fmMsg = g.getFontMetrics();
        int msgX = (width - fmMsg.stringWidth(msg)) / 2;
        g.drawString(msg, msgX, height / 2);

        // Istruzioni
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(new Color(150, 150, 150));
        String hint = "Premi ESC per tornare al menu";
        FontMetrics fmHint = g.getFontMetrics();
        int hintX = (width - fmHint.stringWidth(hint)) / 2;
        g.drawString(hint, hintX, height - 40);
    }

    public static void main(String[] args) {
        Logger.info("JavaQuest 2D Main avviato");
        JFrame frame = new JFrame("JavaQuest");
        Main mainPanel = new Main();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
