package game;

import entities.Player;
import scene.Scene;

public class Game {
    private Scene scene;
    private Player player;
    private int[][] map; // 0=vuoto, 1=ostacolo
    private int tileSize = 32;

    public Game() {
        this.scene = new Scene();
        this.player = new Player(64, 64);
        loadMap();
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

    public int[][] getMap() {
        return map;
    }

    public int getTileSize() {
        return tileSize;
    }

    public Scene getScene() {
        return scene;
    }

    public Player getPlayer() {
        return player;
    }

    // Controllo collisione con ostacoli
    public boolean isBlocked(int px, int py) {
        int tx = px / tileSize;
        int ty = py / tileSize;
        if (tx < 0 || ty < 0 || ty >= map.length || tx >= map[0].length)
            return true;
        return map[ty][tx] == 1;
    }
}