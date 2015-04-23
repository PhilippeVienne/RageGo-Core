package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ragego.engine.HumanPlayer;
import com.ragego.gui.GraphicTurnListener;
import com.ragego.gui.RageGoGame;
import com.ragego.network.OnlineGame;
import com.ragego.network.RageGoServer;

public class OnlineScreen extends ScreenAdapter{
    private static final String TAG = "WidgetsSample";
    private static final int SCENE_WIDTH = 1280;
    private static final int SCENE_HEIGHT = 720;

    private Viewport viewport;
    private Label offlineLabel, yourCodeLabel,  hisCodeLabel;
    private BitmapFont font;
    private Button validateButton, cancelButton;
    private TextField tf;
    private String yourCode = "7ddA9L";
    private String hisCode = "9HCb8f";

    private Stage stage;

    private AssetManager manager;
    private GoGameScreen goGameScreen = new GoGameScreen();

    private Texture tfSelection, tfBackground, tfCursor;

    public OnlineScreen() {
    }

    @Override
    public void show () {
        if (RageGoServer.getLocalPlayer() == null)
            RageGoServer.updateLocalPlayer(new HumanPlayer("Player 1", new GraphicTurnListener(goGameScreen)));
        yourCode = RageGoServer.getLocalPlayer().getCode();
        RageGoServer.addListener(new RageGoServer.NewGameListener() {
            @Override
            public void newGame(OnlineGame game) {
                hisCode = game.getBlacks().getCode();
                RageGoGame.getInstance().setScreen(goGameScreen);
            }
        });
        viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        manager = new AssetManager(new FileHandleResolver() {
            @Override
            public FileHandle resolve(String fileName) {
                return Gdx.files.classpath(fileName);
            }
        });
        manager.setLoader(TiledMap.class, new TmxMapLoader(new FileHandleResolver() {
            @Override
            public FileHandle resolve(String fileName) {
                return Gdx.files.classpath(fileName);
            }
        }));
        manager.load("com/ragego/gui/fonts/acme_9_regular.fnt", BitmapFont.class);
        manager.load("com/ragego/gui/temp/tfSelection.png", Texture.class);
        manager.load("com/ragego/gui/temp/tfbackground.png", Texture.class);
        manager.load("com/ragego/gui/temp/cursor.png", Texture.class);

        manager.finishLoading();
        Gdx.app.log(TAG, "Assets loaded");
        font = manager.get("com/ragego/gui/fonts/acme_9_regular.fnt");

        int middlepointX = SCENE_WIDTH >> 1;
        int middlepointY = SCENE_HEIGHT >> 1;

        // Text labels
        Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);
        offlineLabel = new Label("You are not connected to the Internet" +
                "\n You shall not pass !", ls);

        yourCodeLabel = new Label("Your code is : " + yourCode, ls);
        hisCodeLabel = new Label("Your partner's code : ", ls);

        yourCodeLabel.setPosition(middlepointX - (yourCodeLabel.getWidth()*0.5f), middlepointY - (yourCodeLabel.getHeight()*0.5f));
        hisCodeLabel.setPosition(yourCodeLabel.getX(), yourCodeLabel.getY() - hisCodeLabel.getHeight());

        //Single line of text input
        TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
        tfs.font = font;
        tfs.fontColor = Color.BLACK;
        tfSelection = manager.get("com/ragego/gui/temp/tfSelection.png");
        tfBackground = manager.get("com/ragego/gui/temp/tfbackground.png");
        tfCursor = manager.get("com/ragego/gui/temp/cursor.png");
        tfs.selection = new TextureRegionDrawable(new TextureRegion(tfSelection));
        tfs.background = new TextureRegionDrawable(new TextureRegion(tfBackground));
        tfs.cursor = new TextureRegionDrawable(new TextureRegion(tfCursor));
        tf = new TextField("", tfs);
        tf.setWidth(250);
        tf.setMaxLength(6);
        tf.setOnlyFontChars(true);
        tf.setAlignment(1); //Centers the entered text
        tf.setTextFieldListener(new TextField.TextFieldListener() {
            public void keyTyped (TextField textField, char key) {
                if (key == '\n') textField.getOnscreenKeyboard().show(false);
            }
        });
        tf.setPosition(hisCodeLabel.getX() + hisCodeLabel.getWidth(),
                hisCodeLabel.getY() + hisCodeLabel.getHeight() * 0.5f - tf.getHeight() * 0.5f);

        //Validation and cancellation buttons


        //Adds actors to scene
        stage.addActor(yourCodeLabel);
        stage.addActor(hisCodeLabel);
        stage.addActor(tf);
        //stage.setDebugAll(true);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        manager.dispose();
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