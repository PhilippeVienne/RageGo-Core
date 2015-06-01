package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ragego.gui.RageGoAssetManager;
import com.ragego.gui.RageGoGame;
import com.ragego.gui.elements.HexagonalMenu;
import com.ragego.gui.elements.HexagonalMenuButton;
import com.ragego.gui.elements.RageGoDialog;

import java.util.HashMap;

/**
 * Manages the display of the Main Menu Screen.
 */
public class MenuScreen extends ScreenAdapter implements MusicalScreen{
    /**
     * List buttons shown on this screen. This is used to mask or show good buttons.
     */
    private HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
    /**
     * Viewport to display the menu buttons and message dialogs.
     */
    private ScreenViewport hudViewport;
    /**
     * Viewport to display a simple background image.
     */
    private FillViewport backViewport;
    private Stage backStage, hudStage;
    /**
     * Menu instance used to create buttons and manage them.
     */
    private HexagonalMenu menu;
    /**
     * Skin used for this menu.
     */
    private Skin menuSkin;
    /**
     * When the play button is pressed, this screen will be displayed. If it's null, then the screen is not changed.
     */
    private ScreenAdapter nextScreen = null;
    /**
     * Life is better with music.
     */
    private Music backgroundMusic;

    public MenuScreen() {
        super();
    }

    @Override
    public void show () {
        OrthographicCamera backCamera = new OrthographicCamera();
        OrthographicCamera hudCamera = new OrthographicCamera();
        backViewport = new FillViewport(2048, 1380, backCamera);
        hudViewport = new ScreenViewport(hudCamera);

        backStage = new Stage(backViewport);
        hudStage = new Stage(hudViewport);
        //hudStage.setDebugAll(true);
        Gdx.input.setInputProcessor(hudStage);

        RageGoAssetManager manager = RageGoGame.getAssetManager();
        manager.load("com/ragego/gui/menu/menu.json", Skin.class);
        manager.load("com/ragego/gui/splash/island_background.png", Texture.class);
        manager.finishLoading();
        Gdx.app.log(MenuScreen.class.getCanonicalName(), "Assets loaded");
        menuSkin = manager.get("com/ragego/gui/menu/menu.json");

        Texture backTex = manager.get("com/ragego/gui/splash/island_background.png");
        backTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image backGroundImg = new Image(backTex);
        backStage.addActor(backGroundImg);

        menu = new HexagonalMenu(menuSkin);
        menu.setPosition((hudViewport.getScreenWidth() - menu.getWidth()) * 0.5f,
                (hudViewport.getScreenHeight() - menu.getHeight()) * 0.5f);

        // Play Button
        final HexagonalMenuButton playButton = new HexagonalMenuButton(menu, HexagonalMenu.Position.CENTER, "play");
        buttons.put(0, playButton);
        playButton.setDisabled(true);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isAButtonChecked())
                    play();
            }
        });

        //Return Button
        final HexagonalMenuButton returnButton = new HexagonalMenuButton(menu, HexagonalMenu.Position.BOTTOM, "return");
        buttons.put(1, returnButton);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uncheckOtherButtons(1);
                buttons.get(0).setDisabled(true);
                new RageGoDialog("Exit", RageGoDialog.CONFIRM, new Runnable() {
                    @Override
                    public void run() {
                        Gdx.app.exit();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {

                    }
                },"You are quitting RageGo","Are you sure ?").centerOnViewport(hudViewport).displayOn(hudStage);
            }
        });

        //Solo Button
        final HexagonalMenuButton soloButton = new HexagonalMenuButton(menu, HexagonalMenu.Position.TOP, "solo");
        buttons.put(2, soloButton);
        soloButton.setDisabled(true);
        soloButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (soloButton.isChecked()) {
                    uncheckOtherButtons(2);
                    buttons.get(0).setDisabled(false);
                    Gdx.app.log(MenuScreen.class.getCanonicalName(), "Solo Button clicked");
                } else
                    buttons.get(0).setDisabled(true);
            }
        });

        //Online Button
        final HexagonalMenuButton onlineButton = new HexagonalMenuButton(menu, HexagonalMenu.Position.RIGHT_TOP, "online");
        buttons.put(3, onlineButton);
        onlineButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onlineButton.isChecked()) {
                    uncheckOtherButtons(3);
                    buttons.get(0).setDisabled(false);
                    nextScreen = new OnlineScreen();
                } else
                    buttons.get(0).setDisabled(true);
            }
        });

        //Credits Button
        final HexagonalMenuButton creditsButton = new HexagonalMenuButton(menu, HexagonalMenu.Position.RIGHT_BOTTOM, "credits");
        buttons.put(4, creditsButton);
        creditsButton.setDisabled(true);
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (creditsButton.isChecked()) {
                    uncheckOtherButtons(4);
                    buttons.get(0).setDisabled(false);
                    Gdx.app.log(MenuScreen.class.getCanonicalName(), "Credits Button clicked");
                } else
                    buttons.get(0).setDisabled(true);
            }
        });

        //Multiplayer Button
        final HexagonalMenuButton multiPlayerButton = new HexagonalMenuButton(menu, HexagonalMenu.Position.LEFT_TOP, "multiplayer");
        buttons.put(5, multiPlayerButton);
        multiPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (multiPlayerButton.isChecked()) {
                    uncheckOtherButtons(5);
                    buttons.get(0).setDisabled(false);
                    nextScreen = new MultiplayerScreen();
                } else
                    buttons.get(0).setDisabled(true);
            }
        });

        //Parameters Button
        final HexagonalMenuButton parametersButton = new HexagonalMenuButton(menu, HexagonalMenu.Position.LEFT_BOTTOM, "settings");
        buttons.put(6, parametersButton);
        parametersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (parametersButton.isChecked()) {
                    uncheckOtherButtons(6);
                    buttons.get(0).setDisabled(false);
                    nextScreen = new GuiTestScreen();
                } else
                    buttons.get(0).setDisabled(true);
            }
        });
        hudStage.addActor(menu);
    }

    /**
     * Act user has pressed on play button.
     */
    private void play() {
        if (nextScreen != null) {
            RageGoGame.getInstance().load(nextScreen);
        }
    }

    /**
     * Unchecked all buttons (go to their white version)
     * @param buttonValue Button to leave checked.
     */
    private void uncheckOtherButtons(int buttonValue) {
        for (int i = 2; i < buttons.size(); i++) {
            if (i != buttonValue)
                buttons.get(i).setChecked(false);
        }
    }

    /**
     * Find if one or more button are checked.
     * @return true if there is one (or more) button checked, false otherwise.
     */
    private boolean isAButtonChecked() {
        for (int i = 2; i < buttons.size(); i++) {
            if (buttons.get(i).isChecked()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        backViewport.update(width, height);
        hudViewport.update(width, height, true);

        menu.setPosition((hudViewport.getWorldWidth() - menu.getWidth()) * 0.5f,
                (hudViewport.getWorldHeight() - menu.getHeight()) * 0.5f);
    }

    @Override
    public void dispose() {
        menuSkin.dispose();
        hudStage.dispose();
        backStage.dispose();
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        backStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        backStage.draw();

        hudStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        hudStage.draw();
    }

    @Override
    public void resume() {
        super.resume();
        this.nextScreen = null;
    }

    @Override
    public Music getBackgroundMusic() {
        return backgroundMusic;
    }

    @Override
    public void playMusic() {
        if(backgroundMusic == null){
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("com/ragego/gui/music/Celestial_Aeon_Project_-_Inspiring.mp3"));
        }
        backgroundMusic.setVolume(0f);
        new Thread("UpMySound"){
            @Override
            public void run() {
                while (backgroundMusic.getVolume()<0.2f){
                    backgroundMusic.setVolume(backgroundMusic.getVolume()+0.02f);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        backgroundMusic.setVolume(0.2f);
                    }
                }
            }
        }.start();
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
    }

    @Override
    public void stopMusic() {
        if(backgroundMusic!=null)
            new Thread("UpMySound"){
                @Override
                public void run() {
                    while (backgroundMusic.getVolume()<0f){
                        backgroundMusic.setVolume(backgroundMusic.getVolume()-0.02f);
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            backgroundMusic.setVolume(0f);
                        }
                    }
                    backgroundMusic.stop();
                }
            }.start();
    }
}