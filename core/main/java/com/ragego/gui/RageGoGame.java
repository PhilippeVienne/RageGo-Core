package com.ragego.gui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ragego.gui.screens.MenuScreen;
import com.ragego.utils.InternetCheckThread;

/**
 * Describes how we start the game.
 */
public class RageGoGame extends Game {
    private static final String TAG = "RageGoGame";
    private static final RageGoGame instance = new RageGoGame();
    private static final RageGoAssetManager manager = new RageGoAssetManager();
    private static Skin uiSkin;
    private MenuScreen homeScreen = null;
    private InternetCheckThread checkInternetConnection = new InternetCheckThread();

    private RageGoGame() {
        super();
        checkInternetConnection.start();
    }

    public static RageGoGame getInstance() {
        return instance;
    }

    public static RageGoAssetManager getAssetManager() {
        return manager;
    }

    public static void loadScreen(ScreenAdapter nextScreen) {
        getInstance().load(nextScreen);
    }

    public static void goHome() {
        getInstance().load(getInstance().getHomeScreen());
    }

    public static Skin getUiSkin() {
        return uiSkin;
    }

    @Override
    public void create() {
        Gdx.input.setCatchBackKey(true);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        manager.load("com/ragego/gui/ui/ui.json", Skin.class);
        manager.finishLoading();
        Gdx.app.log(TAG, "Skin loaded");
        uiSkin = manager.get("com/ragego/gui/ui/ui.json");

        homeScreen = new MenuScreen();
        setScreen(homeScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        manager.dispose();
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
        if (nextScreen != null) {
            setScreen(nextScreen);
            screen.resume();
        }
    }

    public MenuScreen getHomeScreen() {
        return homeScreen;
    }

    public boolean isConnected() {
        return checkInternetConnection.isConnected();
    }
}
