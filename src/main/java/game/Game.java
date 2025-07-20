package game;

import core.Window;
import scene.Scene;
import entities.Player;
import utils.Logger;

public class Game {
    private Scene scene;
    private Player player;

    public Game() {
        // Costruttore: inizializza scene, player, ecc.
        this.scene = new Scene();
        this.player = new Player(0, 0);
    }

    public void start(Window window) {
        Logger.info("Game started!");
        // Eventuale ciclo di gioco se vuoi
    }

    public Scene getScene() {
        return scene;
    }

    public Player getPlayer() {
        return player;
    }

    // Eventuali altri metodi per la gestione del gioco
}