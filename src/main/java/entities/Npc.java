package entities;

public class Npc extends Entity {
    public Npc(int x, int y) {
        super(x, y, 32, 32, 1);
    }

    @Override
    public void update() {
        // Logica di aggiornamento NPC
    }

    @Override
    public void render(java.awt.Graphics g) {
        // Logica di rendering NPC
    }
}
