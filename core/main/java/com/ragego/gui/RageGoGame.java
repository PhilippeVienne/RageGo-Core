package com.ragego.gui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.ragego.gui.screens.MenuScreen;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Describes how we start the game.
 */
public class RageGoGame extends Game {

    private static final RageGoGame instance = new RageGoGame();
    private static final RageGoAssetManager manager = new RageGoAssetManager();
    private MenuScreen homeScreen = null;
    private boolean connected = false;
    private Thread checkInternetConnection = new Thread(new Runnable() {
        @Override
        public void run() {
            while (Thread.currentThread().isAlive()) {
                try {
                    connected = isInternetReachable();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    checkInternetConnection.start();
                }
            }
        }

        public boolean isInternetReachable() {
            try {
                //make a URL to a known source
                URL url = new URL("http://www.google.com");

                //open a connection to that source
                HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
                urlConnect.setConnectTimeout(2000);

                //trying to retrieve data from the source. If there
                //is no connection, this line will fail
                int read = urlConnect.getInputStream().read();
                return read != -1;
            } catch (UnknownHostException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
    }, "RageGo-CheckInternet");

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

    @Override
    public void create() {
        Gdx.input.setCatchBackKey(true);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
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
        return connected;
    }
}
