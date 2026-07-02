package ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import game.Main;

public class MenuScreenshot {

    public static void main(String[] args) throws Exception {
        Main panel = new Main();
        panel.setSize(640, 480);

        BufferedImage image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        panel.paint(g);
        g.dispose();

        Path outDir = Path.of("/opt/cursor/artifacts");
        Files.createDirectories(outDir);
        File outFile = outDir.resolve("menu-screenshot.png").toFile();
        ImageIO.write(image, "png", outFile);
        System.out.println("Screenshot salvato: " + outFile.getAbsolutePath());
        System.exit(0);
    }
}
