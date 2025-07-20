package game;

import core.Engine;
import core.IAppLogic;
import core.Window;
import entities.Player;
import entities.Entity;
import input.InputHandler;
import rendering.Render;
import rendering.ui.TextRenderer;
import scene.Scene;
import utils.Logger;

public class Main implements IAppLogic {
    private InputHandler inputHandler;
    private GameStateManager gameStateManager;

    public static void main(String[] args) {
        GameConfig config = GameConfig.getInstance();

        Main main = new Main();

        Window.WindowOptions opts = config.createWindowOptions();

        Engine gameEng = new Engine(config.getWindowTitle(), opts, main);
        gameEng.start();
    }

    @Override
    public void init(Window window, Scene scene, Render render) {
        initWorld(scene);
        initPlayer(scene);
        initUI(window);
        initInputHandler();

        gameStateManager = new GameStateManager(scene, render);

        logSystemInfo();
    }

    private void initWorld(Scene scene) {
        GameConfig config = GameConfig.getInstance();
        scene.setWorld(new World(config.getInitialMap())); // Carica la mappa 2D
        Logger.info("World generated");
    }

    private void initPlayer(Scene scene) {
        GameConfig config = GameConfig.getInstance();
        scene.setPlayer(new Player(config.getPlayerStartX(), config.getPlayerStartY()));
        Logger.info("Player positioned");
    }

    private void initUI(Window window) {
        window.setTextRenderer(new TextRenderer());
        Logger.info("UI initialized");
    }

    private void initInputHandler() {
        inputHandler = new InputHandler();
        Logger.info("Input handler initialized");
    }

    private void logSystemInfo() {
        Logger.info("Game running in 2D mode");
    }

    @Override
    public void input(Window window, Scene scene, Render render, float diffTimeMillis) {
        switch (gameStateManager.getCurrentState()) {
            case PLAYING:
                inputHandler.handleInput(window, scene, diffTimeMillis);
                if (window.isKeyJustPressed("ESCAPE")) {
                    gameStateManager.togglePause();
                }
                break;
            case PAUSED:
                if (window.isKeyJustPressed("ESCAPE")) {
                    gameStateManager.togglePause();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void update(Window window, Scene scene, Render render) {
        gameStateManager.update(diffTimeMillis);
    }

    @Override
    public void cleanup() {
        Logger.info("Cleaning resources...");
    }
}