package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ragego.engine.HumanPlayer;
import com.ragego.gui.RageGoGame;
import com.ragego.network.OnlineGame;
import com.ragego.network.OnlinePlayer;
import com.ragego.network.RageGoServer;
import com.ragego.network.RageGoServerException;

public class OnlineScreen extends ScreenAdapter {
    private static final String TAG = "OnlineScreen";
    private static final int SCENE_WIDTH = 1280;
    private static final int SCENE_HEIGHT = 720;

    private Viewport viewport;
    private TextField tf;
    private String yourCode = "loading ...";

    private Stage stage;
    private Stage offlineStage;

    private AssetManager manager;
    private Label yourCodeLabel;
    private boolean codeLoaded = false;
    private boolean connectedToInternet = true;

    public OnlineScreen() {
        Thread loadUserCodeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!RageGoGame.getInstance().isConnected()) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        RageGoGame.goHome();
                    }
                }
                try {
                    if (RageGoServer.getLocalPlayer() == null)
                        RageGoServer.updateLocalPlayer(new HumanPlayer("Player 1", null));
                    yourCode = RageGoServer.getLocalPlayer().getCode();
                    codeLoaded = true;
                    RageGoServer.addListener(new RageGoServer.NewGameListener() {
                        @Override
                        public void newGame(final OnlineGame game) {
                            final RageGoServer.NewGameListener listener = this;
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    RageGoGame.getInstance().setScreen(new OnlineGoGameScreen(game));
                                    RageGoServer.join(game);
                                    RageGoServer.removeListener(listener);
                                }
                            });
                        }
                    });
                    RageGoServer.startWaitingForGame();
                } catch (Exception e) {
                    connectedToInternet = false;
                }
            }
        }, "LoadUserCode");
        loadUserCodeThread.start();
    }

    @Override
    public void show() {
        viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT);
        stage = new Stage(viewport);
        offlineStage = new Stage(viewport);
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, offlineStage));
        loadAssets();
        BitmapFont font = manager.get("com/ragego/gui/fonts/acme_9_regular.fnt");

        int middlepointX = SCENE_WIDTH >> 1;
        int middlepointY = SCENE_HEIGHT >> 1;

        // Text labels
        Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);
        {
            Label offlineLabel = new Label("You are not connected to the Internet" +
                    "\nYou shall not pass !", ls);
            final Button closeButton = generateCancelButton();
            final float height = offlineLabel.getPrefHeight(), width = offlineLabel.getPrefWidth();
            offlineLabel.setPosition(middlepointX - width * 0.5f, middlepointY - height * 0.5f + 10 + closeButton.getPrefHeight());
            offlineLabel.setAlignment(Align.center);
            closeButton.setPosition(middlepointX - closeButton.getPrefWidth() * 0.5f, middlepointY - height * 0.5f - 10 - closeButton.getPrefHeight() * 0.5f);
            offlineStage.addActor(closeButton);
            offlineStage.addActor(offlineLabel);
        }

        yourCodeLabel = new Label("Your code is loading ...", ls);
        Label hisCodeLabel = new Label("Your partner's code : ", ls);

        //Single line of text input
        TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
        tfs.font = font;
        tfs.fontColor = Color.BLACK;
        Texture tfSelection = manager.get("com/ragego/gui/temp/tfSelection.png");
        Texture tfBackground = manager.get("com/ragego/gui/temp/tfbackground.png");
        Texture tfCursor = manager.get("com/ragego/gui/temp/cursor.png");
        tfs.selection = new TextureRegionDrawable(new TextureRegion(tfSelection));
        tfs.background = new TextureRegionDrawable(new TextureRegion(tfBackground));
        tfs.cursor = new TextureRegionDrawable(new TextureRegion(tfCursor));
        tf = new TextField("", tfs);
        tf.setWidth(250);
        tf.setMaxLength(5);
        tf.setOnlyFontChars(true);
        tf.setAlignment(1); //Centers the entered text
        tf.setTextFieldListener(new TextField.TextFieldListener() {
            public void keyTyped(TextField textField, char key) {
                if (key == '\n') textField.getOnscreenKeyboard().show(false);
            }
        });

        //Validation and cancellation buttons
        Texture validateButtonUpTex = manager.get("com/ragego/gui/menu/button_validate_up.png");
        Texture validateButtonDownTex = manager.get("com/ragego/gui/menu/button_validate_down.png");
        Button.ButtonStyle validateButtonStyle = new Button.ButtonStyle();
        validateButtonStyle.up = new TextureRegionDrawable(new TextureRegion(validateButtonUpTex));
        validateButtonStyle.down = new TextureRegionDrawable(new TextureRegion(validateButtonDownTex));
        Button validateButton = new Button(validateButtonStyle);
        validateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String temp = tf.getText();
                if (temp.matches("[A-La-l2-9]{5}") && !temp.equals(yourCode)) {
                    try {
                        final OnlinePlayer player = RageGoServer.getPlayer(temp);
                        final OnlineGame game = RageGoServer.createGame(RageGoServer.getLocalPlayer(), player);
                        OnlineGoGameScreen screen = new OnlineGoGameScreen(game);
                        RageGoServer.stopWaitingForGame();
                        RageGoGame.loadScreen(screen);
                    } catch (RageGoServerException exception) {
                        System.out.println("No player for code: " + temp);
                    }
                }
            }
        });

        Button cancelButton = generateCancelButton();

        {
            float messageHeight = yourCodeLabel.getPrefHeight() + hisCodeLabel.getPrefHeight() + validateButton.getPrefHeight();
            float messageWidth = hisCodeLabel.getPrefWidth() + tf.getPrefHeight();
            float messageYStart = middlepointY + messageHeight * 0.3f;
            float messageXStart = middlepointX - messageWidth * 0.5f;
            yourCodeLabel.setPosition(messageXStart, messageYStart);
            hisCodeLabel.setPosition(yourCodeLabel.getX(), yourCodeLabel.getY() - hisCodeLabel.getPrefHeight());
            tf.setPosition(hisCodeLabel.getX() + hisCodeLabel.getPrefWidth(),
                    hisCodeLabel.getY() + hisCodeLabel.getPrefHeight() * 0.5f - tf.getPrefHeight() * 0.5f);
            validateButton.setPosition(middlepointX - validateButton.getPrefWidth() * 1.5f, tf.getY() - validateButton.getPrefHeight());
            cancelButton.setPosition(middlepointX + cancelButton.getPrefWidth() * 1.5f, tf.getY() - cancelButton.getPrefHeight());
        }

        //Adds actors to scene
        stage.addActor(yourCodeLabel);
        stage.addActor(hisCodeLabel);
        stage.addActor(tf);
        stage.addActor(validateButton);
        stage.addActor(cancelButton);
        //stage.setDebugAll(true);
    }

    private Button generateCancelButton() {
        Texture cancelButtonUpTex = manager.get("com/ragego/gui/menu/button_cancel_up.png");
        Texture cancelButtonDownTex = manager.get("com/ragego/gui/menu/button_cancel_down.png");
        Button.ButtonStyle cancelButtonStyle = new Button.ButtonStyle();
        cancelButtonStyle.up = new TextureRegionDrawable(new TextureRegion(cancelButtonUpTex));
        cancelButtonStyle.down = new TextureRegionDrawable(new TextureRegion(cancelButtonDownTex));
        Button cancelButton = new Button(cancelButtonStyle);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RageGoGame.goHome();
            }
        });
        return cancelButton;
    }

    private void loadAssets() {
        manager = RageGoGame.getAssetManager();

        manager.load("com/ragego/gui/fonts/acme_9_regular.fnt", BitmapFont.class);

        manager.load("com/ragego/gui/temp/tfSelection.png", Texture.class);
        manager.load("com/ragego/gui/temp/tfbackground.png", Texture.class);
        manager.load("com/ragego/gui/temp/cursor.png", Texture.class);

        manager.load("com/ragego/gui/menu/button_validate_up.png", Texture.class);
        manager.load("com/ragego/gui/menu/button_validate_down.png", Texture.class);
        manager.load("com/ragego/gui/menu/button_cancel_up.png", Texture.class);
        manager.load("com/ragego/gui/menu/button_cancel_down.png", Texture.class);

        Gdx.app.log(TAG, "Assets loading ...");
        manager.finishLoading();
        Gdx.app.log(TAG, "Assets loaded");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (RageGoGame.getInstance().isConnected()) {
            if (codeLoaded)
                yourCodeLabel.setText("Your code is " + yourCode);
            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
            stage.draw();
        } else {
            offlineStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
            offlineStage.draw();
        }
    }
}