package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ragego.gui.RageGoAssetManager;
import com.ragego.gui.RageGoGame;
import com.ragego.gui.menu.HexagonalButton;
import com.ragego.gui.menu.HexagonalMenu;

/**
 * Manages the display of the Main Menu Screen.
 */
public class MenuScreen extends ScreenAdapter{
    private static final String TAG = "MenuScreen";
    public OrthographicCamera backCamera, hudCamera;
    private ScreenViewport hudViewport;
    private FillViewport backViewport;
    private Stage stage;
    private HexagonalMenu menu;
    private RageGoAssetManager manager;
    private Skin menuSkin;
    private ScreenAdapter nextScreen = null;

    public MenuScreen() {
        super();
    }

    @Override
    public void show () {
        backCamera = new OrthographicCamera();
        hudCamera = new OrthographicCamera();
        backViewport = new FillViewport(2048, 1380, backCamera);
        hudViewport = new ScreenViewport(hudCamera);

        stage = new Stage(hudViewport);
        //stage.setDebugAll(true);
        Gdx.input.setInputProcessor(stage);

        manager = RageGoGame.getAssetManager();
        manager.load("com/ragego/gui/menu/menu.json", Skin.class);
        manager.finishLoading();
        Gdx.app.log(TAG, "menuSkin loaded");
        menuSkin = manager.get("com/ragego/gui/menu/menu.json");

        menu = new HexagonalMenu(menuSkin, "menu_frame");
        menu.setPosition((hudViewport.getScreenWidth() - menu.getWidth()) * 0.5f, (hudViewport.getScreenHeight() - menu.getHeight()) * 0.5f);


        // Play Button
        HexagonalButton playButton = new HexagonalButton(menu, HexagonalMenu.Position.CENTER, menuSkin, "play");
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                play();
            }
        });

        //Solo Button
        HexagonalButton soloButton = new HexagonalButton(menu, HexagonalMenu.Position.TOP, menuSkin, "solo");
        soloButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Solo Button clicked");
            }
        });

        //Online Button
        HexagonalButton onlineButton = new HexagonalButton(menu, HexagonalMenu.Position.RIGHT_TOP, menuSkin, "online");
        onlineButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                nextScreen = new OnlineScreen();
            }
        });

        //Credits Button
        HexagonalButton creditsButton = new HexagonalButton(menu, HexagonalMenu.Position.RIGHT_BOTTOM, menuSkin, "credits");
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Credits Button clicked");
            }
        });

        //Return Button
        HexagonalButton returnButton = new HexagonalButton(menu, HexagonalMenu.Position.BOTTOM, menuSkin, "return");
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        //Multiplayer Button
        HexagonalButton multiPlayerButton = new HexagonalButton(menu, HexagonalMenu.Position.LEFT_TOP, menuSkin, "multiplayer");
        multiPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                nextScreen = new SimpleGoGameScreen();
            }
        });

        //Parameters Button
        HexagonalButton parametersButton = new HexagonalButton(menu, HexagonalMenu.Position.LEFT_BOTTOM, menuSkin, "settings");
        parametersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                nextScreen = new GuiTestScreen();
            }
        });
        stage.addActor(menu);
    }

    private void play() {
        if (nextScreen != null) {
            RageGoGame.getInstance().load(nextScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        hudViewport.update(width, height);
    }

    @Override
    public void dispose() {
        menuSkin.dispose();
        stage.dispose();
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    @Override
    public void resume() {
        super.resume();
        this.nextScreen = null;
    }
}