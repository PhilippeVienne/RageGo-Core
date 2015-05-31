package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ragego.gui.RageGoGame;
import com.ragego.gui.elements.HexaFrameBottom;
import com.ragego.gui.elements.HexaFrameBottomButton;
import com.ragego.gui.elements.HexaFrameTop;
import com.ragego.gui.elements.RageGoDialog;
import com.ragego.gui.objects.Goban;
import com.ragego.utils.GuiUtils;

/**
 * Manages the display of a generic Go Game Screen.
 */
public abstract class GoGameScreen extends ScreenAdapter implements MusicalScreen {

    /**
     * Time in milliseconds to wait until check a new user input.
     */
    private static final int REFRESH_INTERVAL_FOR_USER_INPUT = 10;

    /**
     * Main input processor for this screen.
     */
    protected final GoGameScreenMouseTouchListener gobanInputProcessor = new GoGameScreenMouseTouchListener(this);

    /**
     * Multiplexer for inputs to add more inputs ways.
     */
    protected final InputMultiplexer inputMultiplexer = new InputMultiplexer();

    /**
     * Assets manager used to load the map.
     * @see RageGoGame#getAssetManager() This is the value of this var.
     */
    protected AssetManager manager = RageGoGame.getAssetManager();

    protected TiledMap map;
    protected Goban goban;
    protected IsometricTiledMapRenderer renderer;
    protected OrthographicCamera worldCamera;
    protected ExtendViewport worldViewport;
    protected TiledMapTileLayer gridLayer;
    protected TiledMapTileLayer selection;
    protected TiledMapTile selectionTile;

    // =======================================================================================
    // A lot of properties from the map
    protected float mapPartPixWidth, mapPartPixHeight, tileWidthHalf, tileHeightHalf, yOffset;
    protected int mapHeight, mapWidth;
    protected Vector2 mapPartCenter, topTileWorldCoords, bottomTileWorldCoords, leftTileWorldCoords,
            rightTileWorldCoords;
    // End of a lot of properties
    // =======================================================================================

    protected ScreenViewport hudViewport = new ScreenViewport();
    protected Stage hudStage = new Stage(hudViewport);
    protected Skin hudSkin;
    protected HexaFrameBottom hexaFrameBottom;
    protected Button frameBottomHiddenButton = new Button();
    protected Button frameTopHiddenButton = new Button();
    protected boolean hudVisible = false;
    protected HexaFrameTop hexaFrameTop;
    private Music backgroundMusic;

    /*
        Overridden libgdx methods
     */
    @Override
    public final void show() {
        setupMap();
        setupMapBoundsAndDimensions();
        setupCamera();
        goban = new Goban(this, map);
        setupGoban(goban);
        setupHud();
        hideHud(true);
        setupHudShowButton();
        setupInputs();
    }

    @Override
    public final void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        worldCamera.update();

        renderer.setView(worldCamera);
        renderer.render();

        hudStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        hudStage.draw();
    }

    @Override
    public final void resize(int width, int height) {
        worldViewport.update(width, height);
        worldCamera.update();
        renderer.setView(worldCamera);
        renderer.render();
        hudViewport.update(width, height, true);

        //As Libgdx default behaviour for repositioning is not the one desired
        hexaFrameBottom.setPosition((hudViewport.getScreenWidth() - hexaFrameBottom.getWidth()) * 0.5f, 0);
        hexaFrameTop.setPosition((hudViewport.getScreenWidth() - hexaFrameTop.getWidth()) * 0.5f,
                hudViewport.getScreenHeight() - hexaFrameTop.getHeight());

        frameBottomHiddenButton.setPosition((hudViewport.getScreenWidth() - frameBottomHiddenButton.getWidth()) * 0.5f, 0);
        frameTopHiddenButton.setPosition((hudViewport.getScreenWidth() - frameTopHiddenButton.getWidth()) * 0.5f,
                hudViewport.getScreenHeight() - frameTopHiddenButton.getHeight());
    }

    @Override
    public void pause() {
        if (goban != null) goban.stopGame();
    }

    @Override
    public void resume() {
        if (goban != null) goban.startGame();
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        renderer.dispose();
        hudStage.dispose();
        hudSkin.dispose();
    }

    /*
        Setup methods
     */

    /**
     * Loads the map and sets up its display parameters
     */
    private void setupMap() {
        manager.load("com/ragego/gui/maps/" + getMapToLoad() + ".tmx", TiledMap.class);
        manager.finishLoading();
        Gdx.app.log(getClass().getCanonicalName(), "Map loaded");
        map = manager.get("com/ragego/gui/maps/" + getMapToLoad() + ".tmx");
        renderer = new IsometricTiledMapRenderer(map);
        worldCamera = new OrthographicCamera();
        gridLayer = (TiledMapTileLayer) map.getLayers().get("grid");
        selection = (TiledMapTileLayer) map.getLayers().get("selection");
        final TiledMapTileSet toolTS = map.getTileSets().getTileSet("toolTS");
        selectionTile = toolTS.getTile(toolTS.getProperties().get("firstgid", Integer.class));
    }

    /**
     * Computes the world viewport's size and the position of the camera in order to only to display a particular zone
     * of the map, where the assets are.
     */
    private void setupMapBoundsAndDimensions() {
        tileWidthHalf = map.getProperties().get("tilewidth", Integer.class) * 0.5f;
        tileHeightHalf = map.getProperties().get("tileheight", Integer.class) * 0.5f;
        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);

        //Offset between the actual coordinate system and the world coordinate system on the y-axis
        yOffset = tileHeightHalf;

        //Getting the coordinates of extremum tiles for screen sizing and centering
        Vector2 topTileCoords = new Vector2(Float.parseFloat(map.getProperties().get("maxTopX", String.class)),
                Float.parseFloat(map.getProperties().get("maxTopY", String.class)));
        topTileWorldCoords = GuiUtils.isoToWorld(topTileCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);
        Vector2 bottomTileCoords = new Vector2(Float.parseFloat(map.getProperties().get("maxBottomX", String.class)),
                Float.parseFloat(map.getProperties().get("maxBottomY", String.class)));
        bottomTileWorldCoords = GuiUtils.isoToWorld(bottomTileCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);
        Vector2 leftTileCoords = new Vector2(Float.parseFloat(map.getProperties().get("maxLeftX", String.class)),
                Float.parseFloat(map.getProperties().get("maxLeftY", String.class)));
        leftTileWorldCoords = GuiUtils.isoToWorld(leftTileCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);
        Vector2 rightTileCoords = new Vector2(Float.parseFloat(map.getProperties().get("maxRightX", String.class)),
                Float.parseFloat(map.getProperties().get("maxRightY", String.class)));
        rightTileWorldCoords = GuiUtils.isoToWorld(rightTileCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);

        //Size of the visible part of the map in world units + a padding of one tile
        mapPartPixWidth = rightTileWorldCoords.x - leftTileWorldCoords.x + tileWidthHalf * 2 + tileWidthHalf * 4;
        mapPartPixHeight = topTileWorldCoords.y - bottomTileWorldCoords.y + tileHeightHalf * 4 + tileHeightHalf * 4;

        //Determines the center coordinates of the map's visible part for worldCamera centering
        mapPartCenter = new Vector2((rightTileWorldCoords.x + leftTileWorldCoords.x - 2 * tileWidthHalf) * 0.5f,
                (topTileWorldCoords.y + bottomTileWorldCoords.y - 4 * tileHeightHalf) * 0.5f);
    }

    /**
     * Sets up the world camera according to the data computed in setupMapBoundsAndDimensions() method
     */
    private void setupCamera() {
        //Centers worldCamera on map
        worldCamera.translate(mapPartCenter.x, mapPartCenter.y);

        //Maximizes the map size on screen
        worldCamera.viewportWidth = mapPartPixWidth;
        worldCamera.viewportHeight = mapPartPixHeight;
        worldViewport = new ExtendViewport(mapPartPixWidth, mapPartPixHeight, worldCamera);
    }

    /**
     * This function is called to configure a goban for this GameScreen.
     * In this function you should setup {@link com.ragego.engine.GameBoard} and {@link com.ragego.engine.Player}s for
     * this GoGame screen.
     *
     * @param goban The goban to be set up.
     */
    protected abstract void setupGoban(Goban goban);

    /**
     * Sets up the HUD and its buttons
     */
    protected void setupHud() {
        manager.load("com/ragego/gui/hud/hud.json", Skin.class);
        manager.finishLoading();
        Gdx.app.log(getClass().getCanonicalName(), "Hud assets loaded");
        hudSkin = manager.get("com/ragego/gui/hud/hud.json");

        hexaFrameTop = new HexaFrameTop(hudSkin, RageGoGame.getUiSkin());
        hexaFrameTop.setPosition((hudViewport.getScreenWidth() - hexaFrameTop.getWidth()) * 0.5f,
                hudViewport.getScreenHeight() - hexaFrameTop.getHeight());
        hudStage.addActor(hexaFrameTop);

        hexaFrameBottom = new HexaFrameBottom(hudSkin);
        hexaFrameBottom.setPosition((hudViewport.getScreenWidth() - hexaFrameBottom.getWidth()) * 0.5f, 0);
        hudStage.addActor(hexaFrameBottom);

        // Forward Button
        new HexaFrameBottomButton(hexaFrameBottom, 1, "forward").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goban.remakeTurn();
            }
        });

        // Back Button
        new HexaFrameBottomButton(hexaFrameBottom, 2, "back").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goban.cancelLastTurn();
            }
        });

        // Mark Button
        new HexaFrameBottomButton(hexaFrameBottom, 3, "mark").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        // Pass Button
        new HexaFrameBottomButton(hexaFrameBottom, 4, "pass").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goban.markTurnAsShouldBePassed();
            }
        });

        // Save Button
        new HexaFrameBottomButton(hexaFrameBottom, 5, "save").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goban.save();
            }
        });

        // Inactive Buttons
        new HexaFrameBottomButton(hexaFrameBottom, 6, "inactive");
        new HexaFrameBottomButton(hexaFrameBottom, 7, "inactive");
        new HexaFrameBottomButton(hexaFrameBottom, 8, "inactive");
        new HexaFrameBottomButton(hexaFrameBottom, 9, "inactive");

        // Settings Button
        new HexaFrameBottomButton(hexaFrameBottom, 10, "settings").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        // Return Button
        new HexaFrameBottomButton(hexaFrameBottom, 11, "return").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final RageGoDialog confirmation = new RageGoDialog("Confirmation", RageGoDialog.CONFIRM, new Runnable() {
                    @Override
                    public void run() {
                        closeScreen();
                    }
                }, null, "Do you really want to exit the game ?");
                confirmation.centerOnViewport(hudViewport).displayOn(hudStage);
            }
        });
    }

    /**
     * Sets up the hidden state buttons of the HUD
     */
    private void setupHudShowButton() {
        frameBottomHiddenButton = new Button(hudSkin, "frame_bottom_hidden");

        ClickListener hudButtonClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hudVisible = !hudVisible;
                showHud();
            }
        };
        hexaFrameBottom.getHiddenButton().addListener(hudButtonClickListener);
        hexaFrameTop.getHiddenButton().addListener(hudButtonClickListener);
    }

    /**
     * Sets up the objects that define how to manage inputs
     */
    private void setupInputs() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexer.addProcessor(hudStage);
        gobanInputProcessor.addToMultiplexer(inputMultiplexer);
    }

    /* ============================================================================================
        Visibility settings
     */

    /**
     * Displays the HUD and hides the hidden state buttons
     */
    void showHud() {
        if (hudVisible) {
            hexaFrameTop.hide(false);
            hexaFrameBottom.hide(false);
        }
    }

    /**
     * Default method called to hide the HUD
     */
    public void hideHud() {
        hideHud(false);
    }

    /**
     * Hides the HUD and displays the hidden state buttons
     *
     * @param force If true, forces the program to hide the HUD
     */
    private void hideHud(boolean force) {
        if (hudVisible || force) {
            hudVisible = false;
            hexaFrameTop.hide(true);
            hexaFrameBottom.hide(true);
        }
    }

    /* ==================================================================================
        Miscellaneous methods
     */

    /**
     * Function called to close the current game and return to the home screen
     */
    public void closeScreen() {
        goban.stopGame();
        RageGoGame.goHome();
    }

    /**
     * Awaits for any user input on the Goban
     * Notice: this function is thread locking.
     *
     * @return The goban coordinates or null if he want to pass his turn
     */
    public Vector2 waitForUserInputOnGoban() {
        Vector2 gobanCoordinates;
        synchronized (gobanInputProcessor) {
            while ((gobanCoordinates = gobanInputProcessor.popLastTouch()) == null && !goban.passTurn()) {
                try {
                    Thread.sleep(0, REFRESH_INTERVAL_FOR_USER_INPUT);
                    gobanInputProcessor.wait(5);
                } catch (InterruptedException e) {
                    Gdx.app.debug("Threads", "GameEngine thread has been closed");
                }
            }
        }
        if (goban.passTurn()) // if returns Null, means the user passes his turn
            return null;
        return gobanCoordinates;
    }

    /**
     * Specify the map to be loaded for this Go Game screen.
     */
    protected abstract String getMapToLoad();

    /*
        Getters and Setters
     */

    /**
     * Gets the worldCamera associated the Go Game Screen.
     *
     * @return The worldCamera
     */
    public OrthographicCamera getWorldCamera() {
        return worldCamera;
    }


    /**
     * Gets the map associated the Go Game Screen.
     *
     * @return The map
     */
    public TiledMap getMap() {
        return map;
    }

    public HexaFrameTop getHexaFrameTop() {
        return hexaFrameTop;
    }

    @Override
    public Music getBackgroundMusic() {
        return backgroundMusic;
    }

    @Override
    public void playMusic() {
        if(backgroundMusic == null){
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("com/ragego/gui/music/Celestial_Aeon_Project_-_Uplifting.mp3"));
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

    /**
     * Display a dialog on the game screen.
     * @param dialog The dialog to display.
     */
    public void displayDialog(RageGoDialog dialog) {
        dialog.centerOnViewport(hudViewport).displayOn(hudStage);
    }
}