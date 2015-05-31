package com.ragego.gui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ragego.gui.screens.MenuScreen;
import com.ragego.gui.screens.MusicalScreen;
import com.ragego.utils.InternetCheckThread;

/**
 * General entry point for all devices. Describes how to start the game.
 * @author Philippe Vienne
 * @since 1.0
 */
public class RageGoGame extends Game {
    private static final String TAG = "RageGoGame";
    /**
     * There is only one instance of RageGoGame in a JVM.
     */
    private static final RageGoGame instance = new RageGoGame();
    /**
     * This manager is used to simple load all assets for the game.
     */
    private static final RageGoAssetManager manager = new RageGoAssetManager();
    /**
     * Object to store preferences.
     */
    private static final Preferences preferences = null;
    /**
     * Skin for the app. It's easier to access from this object.
     */
    private static Skin uiSkin;
    /**
     * Main screen for the app. Saved to not recreate each times we open the home screen.
     */
    private MenuScreen homeScreen = null;
    /**
     * Independent thread to determine if we are connected to Internet.
     */
    private InternetCheckThread checkInternetConnection = new InternetCheckThread();

    /**
     * Construct a new Game representation.
     */
    private RageGoGame() {
        super();
        checkInternetConnection.start();
    }

    /**
     * Getter for the instance
     * @return The unique instance for this Game.
     */
    public static RageGoGame getInstance() {
        return instance;
    }

    /**
     * Getter for the AssetManager
     * @return An asset manager usable for all RageGo files.
     */
    public static RageGoAssetManager getAssetManager() {
        return manager;
    }

    /**
     * Load a new screen. It remove the old screen and load the new one. If there is music, it play sound of the new one
     * and stop the music from the previous screen.
     * @param nextScreen The new screen to load.
     */
    public static void loadScreen(ScreenAdapter nextScreen) {
        getInstance().load(nextScreen);
    }

    /**
     * Launch the home screen. This is useful to go on home screen wherever you are in the code.
     */
    public static void goHome() {
        getInstance().load(getInstance().getHomeScreen());
    }

    /**
     * RageGo elements should use the same ui {@link Skin}
     * @return The skin that should be used for dialogs, buttons ...
     */
    public static Skin getUiSkin() {
        return uiSkin;
    }

    /**
     * Launch our new wonderful world.
     */
    @Override
    public void create() {
        Gdx.input.setCatchBackKey(true);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        manager.load("com/ragego/gui/ui/ui.json", Skin.class);
        manager.finishLoading();
        Gdx.app.log(TAG, "Skin loaded");
        uiSkin = manager.get("com/ragego/gui/ui/ui.json");

        homeScreen = new MenuScreen();
        loadScreen(homeScreen);
    }

    /**
     * We should refresh what we see no ?
     */
    @Override
    public void render() {
        super.render();
    }

    /**
     * If the user want to close the app.
     */
    @Override
    public void dispose() {
        super.dispose();
        manager.dispose();
    }

    /**
     * When the app is resumed (it should have previously be paused).
     */
    @Override
    public void resume() {
        super.resume();
    }

    /**
     * If the user do other things than playing.
     */
    @Override
    public void pause() {
        super.pause();
    }

    /**
     * User resize the screen (or the screen dimension has changed).
     * @param width The new width
     * @param height The new height
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    /**
     * Loads the screen to be displayed
     *
     * @param nextScreen The screen to be displayed
     */
    public void load(ScreenAdapter nextScreen) {
        if (nextScreen != null) {
            if(screen!=null)
                if(screen instanceof MusicalScreen) ((MusicalScreen) screen).stopMusic();
            if(nextScreen instanceof MusicalScreen) ((MusicalScreen) nextScreen).playMusic();
            setScreen(nextScreen);
            screen.resume();
        }
    }

    /**
     * Gets the home screen.
     *
     * @return The home Screen
     */
    public MenuScreen getHomeScreen() {
        return homeScreen;
    }

    /**
     * Checks for the internet connection status
     *
     * @return Whether the game has or hasn't access to the internet
     */
    public boolean isConnected() {
        return checkInternetConnection.isConnected();
    }
}
