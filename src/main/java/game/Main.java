package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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

    public Main() {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        game = new Game();
        player = game.getPlayer();
        render = new Render();
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
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

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_UP
                        || key == KeyEvent.VK_DOWN) {
                    player.stopWalking();
                }
            }
        });
        // Timer per aggiornare animazione e movimento
        new javax.swing.Timer(30, evt -> {
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
            repaint();
        }).start();
    }

    private boolean playerIsMoving() {
        // Ritorna true se il player sta camminando
        return player != null && player.isWalking();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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

    public static void main(String[] args) {
        Logger.info("JavaQuest 2D Main avviato");
        JFrame frame = new JFrame("JavaQuest Main");
        Main mainPanel = new Main();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}