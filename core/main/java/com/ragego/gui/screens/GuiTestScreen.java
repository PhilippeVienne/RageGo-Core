package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ragego.gui.RageGoGame;

/**
 * Screen for debug purposes.
 */
public class GuiTestScreen extends ScreenAdapter {
    private static final String TAG = "MenuScreen";
    protected AssetManager manager;
    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextButton button1;

    public GuiTestScreen() {
        super();
    }

    @Override
    public void show() {
        viewport = new ScreenViewport();
        stage = new Stage(viewport);

        manager = RageGoGame.getAssetManager();
        manager.load("com/ragego/gui/skins/ui_gray.json", Skin.class);
        manager.finishLoading();
        Gdx.app.log(TAG, "Assets loaded");

        skin = manager.get("com/ragego/gui/skins/ui_gray.json");

        button1 = new TextButton("SkinDefaultButton", skin);

        stage.addActor(button1);
        //stage.setDebugAll(true);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    @Override
    public void resume() {
        super.resume();
    }
}
