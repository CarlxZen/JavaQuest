package entities;

public class Enemy extends Entity {
    public Enemy(int x, int y) {
        super(x, y, 32, 32, 1);
    }

    @Override
    public void update() {
        // Logica di aggiornamento nemico
    }

    @Override
    public void render(java.awt.Graphics g) {
        // Logica di rendering nemico
    }
}
