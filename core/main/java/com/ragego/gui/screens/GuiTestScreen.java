package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ragego.gui.RageGoGame;
import com.ragego.gui.elements.RageGoDialog;

/**
 * Screen for debug purposes.
 */
public class GuiTestScreen extends ScreenAdapter {
    private static final String TAG = "MenuScreen";

    private static final int SCENE_WIDTH = 1280;
    private static final int SCENE_HEIGHT = 720;

    protected AssetManager manager;
    private Viewport backViewport;
    private ScreenViewport hudViewport;
    private Stage backStage;
    private Skin skin;
    private Dialog yesNoWindow;
    private Image backGroundImg;
    private Texture backTex;
    private OrthographicCamera backCamera;
    private OrthographicCamera hudCamera;
    private Stage hudStage;

    public GuiTestScreen() {
        super();
        manager = RageGoGame.getAssetManager();
        skin = RageGoGame.getUiSkin();
    }

    @Override
    public void show() {
        backCamera = new OrthographicCamera();
        hudCamera = new OrthographicCamera();
        backViewport = new FillViewport(2048, 1380, backCamera);
        hudViewport = new ScreenViewport(hudCamera);
        hudViewport.setUnitsPerPixel(2);

        backStage = new Stage(backViewport);
        hudStage = new Stage(hudViewport);

        manager.load("com/ragego/gui/splash/island_background.png", Texture.class);
        manager.finishLoading();
        Gdx.app.log(TAG, "Assets loaded");

        backTex = manager.get("com/ragego/gui/splash/island_background.png");
        backTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Table layerBackground = new Table();
        backGroundImg = new Image(backTex);
        layerBackground.add(backGroundImg);
        backStage.addActor(backGroundImg);

        //Window
        Label message = new Label("Dialog: Exit?", skin);
        TextButton tb1 = new TextButton("Yes", skin);
        tb1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Yes button clicked");
                RageGoGame.goHome();
            }
        });
        TextButton tb2 = new TextButton("No", skin);
        tb2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "No button clicked");
            }
        });

        yesNoWindow = new Dialog("", skin);
        yesNoWindow.getContentTable().row().colspan(1).center();
        yesNoWindow.getContentTable().add(message);
        yesNoWindow.row().colspan(2);
        yesNoWindow.button(tb1);
        yesNoWindow.button(tb2);
        yesNoWindow.setKeepWithinStage(true);
        yesNoWindow.setResizable(true);
        yesNoWindow.pack();
        yesNoWindow.setPosition((hudViewport.getWorldWidth() - yesNoWindow.getWidth()) * 0.5f,
                (hudViewport.getWorldHeight() - yesNoWindow.getHeight()) * 0.5f);
        //hudStage.addActor(yesNoWindow);

        WidgetGroup widgetGroup = new WidgetGroup();
        widgetGroup.addActor(tb1);
        widgetGroup.addActor(tb2);
        widgetGroup.pack();
        widgetGroup.setPosition(0, 0);
        hudStage.addActor(widgetGroup);
        widgetGroup.setDebug(true, true);

        //TEST
        RageGoDialog messageDialog = new RageGoDialog("Erreur", "On a eu un bug ?", RageGoDialog.MESSAGE, new Runnable() {
            @Override
            public void run() {
                System.out.println("Je blague");
                throw new RuntimeException("Ahah ! et si y en a un");
            }
        });
        //messageDialog.centerOnViewport(hudViewport).displayOn(hudStage);

        Gdx.input.setInputProcessor(hudStage);
    }

    @Override
    public void resize(int width, int height) {
        backViewport.update(width, height);
        hudViewport.update(width, height, true);
        yesNoWindow.setPosition((hudViewport.getWorldWidth() - yesNoWindow.getWidth()) * 0.5f,
                (hudViewport.getWorldHeight() - yesNoWindow.getHeight()) * 0.5f);
    }

    @Override
    public void dispose() {
        skin.dispose();
        hudStage.dispose();
        backStage.dispose();
    }

    @Override
    public void render(float delta) {
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
    }
}
