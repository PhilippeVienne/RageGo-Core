package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ragego.gui.RageGoAssetManager;
import com.ragego.gui.RageGoGame;

public class MultiplayerScreen extends ScreenAdapter {
    private static final String TAG = "MultiplayerScreen";
    String mapNameString = "goban_";
    String mapNameStringWithNumber = mapNameString;
    private FillViewport backViewport;
    private ScreenViewport hudViewport;
    private Stage backStage;
    private Stage hudStage;
    private Dialog multiplayerInputDialog;

    @Override
    public void show() {
        OrthographicCamera backCamera = new OrthographicCamera();
        OrthographicCamera hudCamera = new OrthographicCamera();
        backViewport = new FillViewport(2048, 1380, backCamera);
        hudViewport = new ScreenViewport(hudCamera);

        backStage = new Stage(backViewport);
        hudStage = new Stage(hudViewport);
        //hudStage.setDebugAll(true);
        Gdx.input.setInputProcessor(hudStage);

        Skin uiSkin = RageGoGame.getUiSkin();
        RageGoAssetManager manager = RageGoGame.getAssetManager();
        manager.load("com/ragego/gui/splash/island_background.png", Texture.class);
        Gdx.app.log(MenuScreen.class.getCanonicalName(), "Assets loaded");

        Texture backTex = manager.get("com/ragego/gui/splash/island_background.png");
        backTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image backGroundImg = new Image(backTex);
        backStage.addActor(backGroundImg);

        //Input Dialog
        multiplayerInputDialog = new Dialog("Multiplayer Mode", uiSkin);
        Label mapSizeLabel = new Label("Map Size", uiSkin);
        mapSizeLabel.setAlignment(Align.center);
        mapSizeLabel.setFontScale(0.5f);
        Label nineLabel = new Label("9 ", uiSkin);
        nineLabel.setAlignment(Align.center);
        nineLabel.setFontScale(0.5f);
        Label nineteenLabel = new Label("19 ", uiSkin);
        nineteenLabel.setAlignment(Align.center);
        nineteenLabel.setFontScale(0.5f);
        Label seasonLabel = new Label("Season : ", uiSkin);
        seasonLabel.setAlignment(Align.center);
        seasonLabel.setFontScale(0.5f);
        Label summerLabel = new Label("Summer ", uiSkin);
        summerLabel.setAlignment(Align.center);
        summerLabel.setFontScale(0.5f);
        Label autumnLabel = new Label("Autumn ", uiSkin);
        autumnLabel.setAlignment(Align.center);
        autumnLabel.setFontScale(0.5f);
        Label winterLabel = new Label("Winter ", uiSkin);
        winterLabel.setAlignment(Align.center);
        winterLabel.setFontScale(0.5f);

        final CheckBox nineCheckbox = new CheckBox("", uiSkin);
        final CheckBox nineteenCheckbox = new CheckBox("", uiSkin);
        final CheckBox summerCheckBox = new CheckBox("", uiSkin);
        final CheckBox autumnCheckBox = new CheckBox("", uiSkin);
        final CheckBox winterCheckBox = new CheckBox("", uiSkin);

        nineCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (nineteenCheckbox.isChecked()) {
                    nineteenCheckbox.setChecked(false);
                    mapNameString = "Goban_";
                }
                nineCheckbox.setChecked(true);
                mapNameString += "9" + "_";
                mapNameStringWithNumber = mapNameString;
            }
        });
        nineteenCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (nineCheckbox.isChecked()) {
                    nineCheckbox.setChecked(false);
                    mapNameString = "Goban_";
                }
                nineteenCheckbox.setChecked(true);
                mapNameString += "19" + "_";
                mapNameStringWithNumber = mapNameString;
            }
        });
        summerCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (nineCheckbox.isChecked() || nineteenCheckbox.isChecked()) {
                    if (autumnCheckBox.isChecked() || winterCheckBox.isChecked()) {
                        autumnCheckBox.setChecked(false);
                        winterCheckBox.setChecked(false);
                        mapNameString = mapNameStringWithNumber;
                    }
                    summerCheckBox.setChecked(true);
                    mapNameString += "summer";
                } else summerCheckBox.setChecked(false);
            }
        });
        autumnCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (nineCheckbox.isChecked() || nineteenCheckbox.isChecked()) {
                    if (summerCheckBox.isChecked() || winterCheckBox.isChecked()) {
                        summerCheckBox.setChecked(false);
                        winterCheckBox.setChecked(false);
                        mapNameString = mapNameStringWithNumber;
                    }
                    autumnCheckBox.setChecked(true);
                    mapNameString += "autumn";
                } else autumnCheckBox.setChecked(false);
            }
        });
        winterCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (nineCheckbox.isChecked() || nineteenCheckbox.isChecked()) {
                    if (summerCheckBox.isChecked() || autumnCheckBox.isChecked()) {
                        summerCheckBox.setChecked(false);
                        autumnCheckBox.setChecked(false);
                        mapNameString = mapNameStringWithNumber;
                    }
                    winterCheckBox.setChecked(true);
                    mapNameString += "winter";
                } else winterCheckBox.setChecked(false);
            }
        });

        multiplayerInputDialog.getContentTable().add(mapSizeLabel).center().row();
        multiplayerInputDialog.getContentTable().add(nineCheckbox);
        multiplayerInputDialog.getContentTable().add(nineLabel).row();
        multiplayerInputDialog.getContentTable().add(nineteenCheckbox);
        multiplayerInputDialog.getContentTable().add(nineteenLabel).row();
        multiplayerInputDialog.getContentTable().add(seasonLabel).center().row();
        multiplayerInputDialog.getContentTable().add(summerCheckBox);
        multiplayerInputDialog.getContentTable().add(summerLabel).row();
        multiplayerInputDialog.getContentTable().add(autumnCheckBox);
        multiplayerInputDialog.getContentTable().add(autumnLabel).row();
        multiplayerInputDialog.getContentTable().add(winterCheckBox);
        multiplayerInputDialog.getContentTable().add(winterLabel).row();

        TextButton okButton = new TextButton("Ok", uiSkin);
        TextButton cancelButton = new TextButton("Cancel", uiSkin);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if ((summerCheckBox.isChecked() || autumnCheckBox.isChecked() || winterCheckBox.isChecked()) &&
                        (nineCheckbox.isChecked() || nineteenCheckbox.isChecked())) {
                    RageGoGame.loadScreen(new SimpleGoGameScreen(mapNameString));
                }
            }
        });
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RageGoGame.goHome();
            }
        });
        okButton.getLabel().setFontScale(0.5f);
        cancelButton.getLabel().setFontScale(0.5f);
        multiplayerInputDialog.getButtonTable().add(okButton).width(200);
        multiplayerInputDialog.getButtonTable().add(cancelButton).width(200);

        multiplayerInputDialog.pack();
        multiplayerInputDialog.setPosition((hudViewport.getWorldWidth() - multiplayerInputDialog.getWidth()) * 0.5f,
                (hudViewport.getWorldHeight() - multiplayerInputDialog.getHeight()) * 0.5f);
        hudStage.addActor(multiplayerInputDialog);
        multiplayerInputDialog.setVisible(true);
    }

    @Override
    public void resize(int width, int height) {
        backViewport.update(width, height);
        hudViewport.update(width, height, true);

        multiplayerInputDialog.setPosition((hudViewport.getWorldWidth() - multiplayerInputDialog.getWidth()) * 0.5f,
                (hudViewport.getWorldHeight() - multiplayerInputDialog.getHeight()) * 0.5f);
    }

    @Override
    public void dispose() {
        backStage.dispose();
        hudStage.dispose();
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
}
