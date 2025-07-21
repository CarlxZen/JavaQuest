package scene;

import entities.Entity;

public class Scene {
    private Entity player;

    public Scene() {
        // Costruttore vuoto
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    public Entity getPlayer() {
        return player;
    }
}
