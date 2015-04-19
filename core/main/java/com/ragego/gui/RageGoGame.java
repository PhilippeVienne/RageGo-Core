package com.ragego.gui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.ragego.gui.screens.MenuScreen;

/**
 * Describes how we start the game.
 */
public class RageGoGame extends Game {

    private static RageGoGame instance = new RageGoGame();

    private RageGoGame() {
        super();
    }

    public static RageGoGame getInstance() {
        return instance;
    }

    private MenuScreen homeScreen = null;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        homeScreen = new MenuScreen(this);
        setScreen(homeScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    public void load(ScreenAdapter nextScreen) {
        if (nextScreen != null)
            setScreen(nextScreen);
    }

    public static void goHome() {
        getInstance().load(getInstance().getHomeScreen());
    }

    public MenuScreen getHomeScreen() {
        return homeScreen;
    }
}
