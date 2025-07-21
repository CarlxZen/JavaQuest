package rendering;

import java.awt.Graphics;
import scene.Scene;

public class Render {
    public void renderScene(Graphics g, Scene scene) {
        if (scene.getPlayer() != null) {
            scene.getPlayer().render(g);
        }
    }
}
