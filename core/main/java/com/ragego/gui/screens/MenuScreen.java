package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ragego.gui.RageGoGame;
import com.ragego.gui.menu.HexagonalButton;
import com.ragego.gui.menu.HexagonalMenu;

/**
 * Manages the display of the Main Menu Screen.
 */
public class MenuScreen extends ScreenAdapter{
    private static final String TAG = "MenuScreen";
    private final RageGoGame rageGoGame;

    private Viewport viewport;

    private Stage stage;

    private HexagonalMenu menu;

    private ScreenAdapter nextScreen = null;

    public MenuScreen(RageGoGame rageGoGame) {
        super();
        this.rageGoGame = rageGoGame;
    }

    @Override
    public void show () {
        viewport = new ScreenViewport();
        stage = new Stage(viewport);
        //stage.setDebugAll(true);
        Gdx.input.setInputProcessor(stage);

        menu = new HexagonalMenu(viewport, stage);

        // Play Button
        HexagonalButton playButton = new HexagonalButton(menu, "com/ragego/gui/menu/button_play.png", HexagonalMenu.Position.CENTER);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                play();
            }
        });

        //Solo Button
        HexagonalButton soloButton = new HexagonalButton(menu, "com/ragego/gui/menu/button_solo.png", HexagonalMenu.Position.TOP);
        soloButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                nextScreen = new GoGameScreen();
            }
        });

        //Online Button
        HexagonalButton onlineButton = new HexagonalButton(menu, "com/ragego/gui/menu/button_online.png", HexagonalMenu.Position.RIGHT_TOP);
        onlineButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Online Button clicked");
            }
        });

        //Credits Button
        HexagonalButton creditsButton = new HexagonalButton(menu, "com/ragego/gui/menu/button_credits.png", HexagonalMenu.Position.RIGHT_BOTTOM);
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Credits Button clicked");
            }
        });

        //Return Button
        HexagonalButton returnButton = new HexagonalButton(menu, "com/ragego/gui/menu/button_return.png", HexagonalMenu.Position.BOTTOM);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Return Button clicked");
            }
        });

        //Multiplayer Button
        HexagonalButton multiPlayerButton = new HexagonalButton(menu, "com/ragego/gui/menu/button_multiplayer.png", HexagonalMenu.Position.LEFT_TOP);
        multiPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Multiplayer Button clicked");
            }
        });

        //Parameters Button
        HexagonalButton parametersButton = new HexagonalButton(menu, "com/ragego/gui/menu/button_parameters.png", HexagonalMenu.Position.LEFT_BOTTOM);
        parametersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Parameters Button clicked");
            }
        });
    }

    private void play() {
        if (nextScreen != null) {
            rageGoGame.load(nextScreen);
        }
    }

    @Override
    public void resize(int width, int height) {viewport.update(width, height);
    }

    @Override
    public void dispose() {
        menu.dispose();
        stage.dispose();
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }
}