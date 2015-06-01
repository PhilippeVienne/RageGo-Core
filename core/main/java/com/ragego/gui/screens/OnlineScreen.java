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
import com.ragego.engine.HumanPlayer;
import com.ragego.gui.RageGoAssetManager;
import com.ragego.gui.RageGoGame;
import com.ragego.gui.elements.RageGoDialog;
import com.ragego.network.OnlineGame;
import com.ragego.network.OnlinePlayer;
import com.ragego.network.RageGoServer;
import com.ragego.network.RageGoServerException;

public class OnlineScreen extends ScreenAdapter {
    private static final String TAG = "OnlineScreen";

    private String yourCode = "loading ...";

    private boolean codeLoaded = false;
    private FillViewport backViewport;
    private ScreenViewport hudViewport;
    private Stage backStage;
    private Stage hudStage;
    private Dialog errorDialog;
    private Dialog onlineInputDialog;
    private Label yourCodeLabel;
    private boolean connectedToInternet;
    private TextField codeField;

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
                                }
                            });
                            RageGoServer.join(game);
                            RageGoServer.removeListener(listener);
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
        Gdx.app.log(OnlineScreen.class.getCanonicalName(), "Assets loaded");

        Texture backTex = manager.get("com/ragego/gui/splash/island_background.png");
        backTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image backGroundImg = new Image(backTex);
        backStage.addActor(backGroundImg);

        //Input Dialog
        onlineInputDialog = new Dialog("Online Mode", uiSkin);
        yourCodeLabel = new Label("Your code is loading ...", uiSkin);
        yourCodeLabel.setAlignment(Align.center);
        yourCodeLabel.setFontScale(0.5f);
        Label partnersCodeLabel = new Label("Your partner's code : ", uiSkin);
        partnersCodeLabel.setAlignment(Align.center);
        partnersCodeLabel.setFontScale(0.5f);
        codeField = new TextField("", uiSkin);
        codeField.setMaxLength(5);
        codeField.setOnlyFontChars(true);
        codeField.setAlignment(1); //Centers the entered text
        codeField.setTextFieldListener(new TextField.TextFieldListener() {
            public void keyTyped(TextField textField, char key) {
                if (key == '\n') textField.getOnscreenKeyboard().show(false);
            }
        });
        onlineInputDialog.getContentTable().add(yourCodeLabel).center().row();
        onlineInputDialog.getContentTable().add(partnersCodeLabel).row();
        onlineInputDialog.getContentTable().add(codeField).width(500);

        TextButton okButton = new TextButton("Ok", uiSkin);
        TextButton cancelButton = new TextButton("Cancel", uiSkin);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String temp = codeField.getText();
                if (temp.matches("[A-La-l2-9]{5}") && !temp.equals(yourCode)) {
                    try {
                        final OnlinePlayer player = RageGoServer.getPlayer(temp);
                        final OnlineGame game = RageGoServer.createGame(RageGoServer.getLocalPlayer(), player);
                        OnlineGoGameScreen screen = new OnlineGoGameScreen(game);
                        RageGoServer.stopWaitingForGame();
                        RageGoGame.loadScreen(screen);
                    } catch (RageGoServerException exception) {
                        new RageGoDialog("Error", "The user " + temp + " is not availaible !").centerOnViewport(hudViewport).displayOn(hudStage);
                    }
                } else if (temp.equals(yourCode)) {
                    new RageGoDialog("Error", "You can not join a game with yourself").centerOnViewport(hudViewport).displayOn(hudStage);
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
        onlineInputDialog.getButtonTable().add(okButton).width(200);
        onlineInputDialog.getButtonTable().add(cancelButton).width(200);

        onlineInputDialog.pack();
        onlineInputDialog.setPosition((hudViewport.getWorldWidth() - onlineInputDialog.getWidth()) * 0.5f,
                (hudViewport.getWorldHeight() - onlineInputDialog.getHeight()) * 0.5f);
        hudStage.addActor(onlineInputDialog);

        //Error Dialog
        errorDialog = new Dialog("Online Mode", uiSkin);
        Label errorLabel = new Label("You are not connected to the Internet" +
                "\nYou shall not pass !", uiSkin);
        errorLabel.setAlignment(Align.center);
        errorLabel.setFontScale(0.5f);
        errorLabel.setWrap(true);
        errorDialog.getContentTable().add(errorLabel).width(500);

        TextButton backToMenuButton = new TextButton("Back to the main Menu", uiSkin);
        backToMenuButton.getLabel().setFontScale(0.5f);
        backToMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RageGoGame.goHome();
            }
        });
        errorDialog.getButtonTable().add(backToMenuButton);

        errorDialog.pack();
        errorDialog.setPosition((hudViewport.getWorldWidth() - errorDialog.getWidth()) * 0.5f,
                (hudViewport.getWorldHeight() - errorDialog.getHeight()) * 0.5f);
        hudStage.addActor(errorDialog);
    }

    @Override
    public void resize(int width, int height) {
        backViewport.update(width, height);
        hudViewport.update(width, height, true);

        errorDialog.setPosition((hudViewport.getWorldWidth() - errorDialog.getWidth()) * 0.5f,
                (hudViewport.getWorldHeight() - errorDialog.getHeight()) * 0.5f);

        onlineInputDialog.setPosition((hudViewport.getWorldWidth() - onlineInputDialog.getWidth()) * 0.5f,
                (hudViewport.getWorldHeight() - onlineInputDialog.getHeight()) * 0.5f);
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
        if (RageGoGame.getInstance().isConnected()) {
            if (codeLoaded)
                yourCodeLabel.setText("Your code is : " + yourCode);
            onlineInputDialog.setVisible(true);
            errorDialog.setVisible(false);
        } else {
            onlineInputDialog.setVisible(false);
            errorDialog.setVisible(true);
        }
    }
}