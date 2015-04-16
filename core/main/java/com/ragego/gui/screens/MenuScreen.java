package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen extends ScreenAdapter{
    private static final String TAG = "MenuScreen";

    private Viewport viewport;

    private Stage stage;

    private Texture menuBackTex, playButtonTex, soloButtonTex, onlineButtonTex,
            creditsButtonTex, returnButtonTex, multiplayerButtonTex, parametersButtonTex;

    @Override
    public void show () {
        viewport = new ScreenViewport();
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        Vector2 screenCenter = new Vector2(viewport.getScreenWidth()*0.5f, viewport.getScreenHeight()*0.5f);

        //Menu Background
        menuBackTex = new Texture(Gdx.files.internal("android/assets/ui/main_menu/menu_back.png"));
        Image menuBack = new Image(menuBackTex);
        Vector2 menuBackCenter = new Vector2(menuBack.getWidth()*0.5f, menuBack.getHeight()*0.5f);

        menuBack.setPosition(screenCenter.x - menuBackCenter.x, screenCenter.y - menuBackCenter.y);

        //Play Button
        playButtonTex = new Texture(Gdx.files.internal("android/assets/ui/main_menu/button_play.png"));
        Button playButton;
        Button.ButtonStyle playButtonStyle = new Button.ButtonStyle();
        playButtonStyle.up = new TextureRegionDrawable(new TextureRegion(playButtonTex));
        playButton = new Button(playButtonStyle);

        playButton.setPosition(screenCenter.x - playButton.getWidth() * 0.5f, screenCenter.y - playButton.getHeight() * 0.5f);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Play Button clicked");
            }
        });

        //Solo Button
        soloButtonTex = new Texture(Gdx.files.internal("android/assets/ui/main_menu/button_solo.png"));
        Button soloButton;
        Button.ButtonStyle soloButtonStyle = new Button.ButtonStyle();
        soloButtonStyle.up = new TextureRegionDrawable(new TextureRegion(soloButtonTex));
        soloButton = new Button(soloButtonStyle);

        soloButton.setPosition(menuBack.getX() + menuBack.getWidth()*0.5f - soloButton.getWidth()*0.5f, menuBack.getY() + menuBack.getHeight()*0.825f - soloButton.getHeight()*0.5f);

        soloButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Solo Button clicked");
            }
        });

        //Online Button
        onlineButtonTex = new Texture(Gdx.files.internal("android/assets/ui/main_menu/button_online.png"));
        Button onlineButton;
        Button.ButtonStyle onlineButtonStyle = new Button.ButtonStyle();
        onlineButtonStyle.up = new TextureRegionDrawable(new TextureRegion(onlineButtonTex));
        onlineButton = new Button(onlineButtonStyle);

        onlineButton.setPosition(menuBack.getX() + menuBack.getWidth()*0.825f - onlineButton.getWidth()*0.5f, menuBack.getY() + menuBack.getHeight()*0.665f - onlineButton.getHeight()*0.5f);

        onlineButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Online Button clicked");
            }
        });

        //Credits Button
        creditsButtonTex = new Texture(Gdx.files.internal("android/assets/ui/main_menu/button_credits.png"));
        Button creditsButton;
        Button.ButtonStyle creditsButtonStyle = new Button.ButtonStyle();
        creditsButtonStyle.up = new TextureRegionDrawable(new TextureRegion(creditsButtonTex));
        creditsButton = new Button(creditsButtonStyle);

        creditsButton.setPosition(menuBack.getX() + menuBack.getWidth()*0.825f - creditsButton.getWidth()*0.5f, menuBack.getY() + menuBack.getHeight()*0.34f - creditsButton.getHeight()*0.5f);

        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Credits Button clicked");
            }
        });

        //Return Button
        returnButtonTex = new Texture(Gdx.files.internal("android/assets/ui/main_menu/button_return.png"));
        Button returnButton;
        Button.ButtonStyle returnButtonStyle = new Button.ButtonStyle();
        returnButtonStyle.up = new TextureRegionDrawable(new TextureRegion(returnButtonTex));
        returnButton = new Button(returnButtonStyle);

        returnButton.setPosition(menuBack.getX() + menuBack.getWidth()*0.5f - returnButton.getWidth()*0.5f, menuBack.getY() + menuBack.getHeight()*0.175f - returnButton.getHeight()*0.5f);

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Return Button clicked");
            }
        });

        //Multiplayer Button
        multiplayerButtonTex = new Texture(Gdx.files.internal("android/assets/ui/main_menu/button_multiplayer.png"));
        Button multiplayerButton;
        Button.ButtonStyle multiplayerButtonStyle = new Button.ButtonStyle();
        multiplayerButtonStyle.up = new TextureRegionDrawable(new TextureRegion(multiplayerButtonTex));
        multiplayerButton = new Button(multiplayerButtonStyle);

        multiplayerButton.setPosition(menuBack.getX() + menuBack.getWidth()*0.175f - multiplayerButton.getWidth()*0.5f, menuBack.getY() + menuBack.getHeight()*0.665f - multiplayerButton.getHeight()*0.5f);

        multiplayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Multiplayer Button clicked");
            }
        });

        //Parameters Button
        parametersButtonTex = new Texture(Gdx.files.internal("android/assets/ui/main_menu/button_parameters.png"));
        Button parametersButton;
        Button.ButtonStyle parametersButtonStyle = new Button.ButtonStyle();
        parametersButtonStyle.up = new TextureRegionDrawable(new TextureRegion(parametersButtonTex));
        parametersButton = new Button(parametersButtonStyle);

        parametersButton.setPosition(menuBack.getX() + menuBack.getWidth() * 0.175f - parametersButton.getWidth() * 0.5f, menuBack.getY() + menuBack.getHeight() * 0.34f - parametersButton.getHeight() * 0.5f);

        parametersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Parameters Button clicked");
            }
        });

        stage.addActor(menuBack);
        stage.addActor(playButton);
        stage.addActor(soloButton);
        stage.addActor(onlineButton);
        stage.addActor(creditsButton);
        stage.addActor(returnButton);
        stage.addActor(parametersButton);
        stage.addActor(multiplayerButton);
    }

    @Override
    public void resize(int width, int height) {viewport.update(width, height);
    }

    @Override
    public void dispose() {
        menuBackTex.dispose();
        playButtonTex.dispose();
        soloButtonTex.dispose();
        onlineButtonTex.dispose();
        creditsButtonTex.dispose();
        returnButtonTex.dispose();
        multiplayerButtonTex.dispose();
        parametersButtonTex.dispose();

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