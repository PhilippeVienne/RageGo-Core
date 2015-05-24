package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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
public abstract class GoGameScreen extends ScreenAdapter {
    private static final String TAG = "GoGameScreen";
    private static final int REFRESH_INTERVAL_FOR_USER_INPUT = 10;
    protected final GoGameScreenMouseTouchListener gobanInputProcessor = new GoGameScreenMouseTouchListener(this);
    protected final InputMultiplexer inputMultiplexer = new InputMultiplexer();
    protected AssetManager manager = RageGoGame.getAssetManager();
    protected TiledMap map;
    protected Goban goban;
    protected float yOffset;
    protected float tileWidthHalf;
    protected float tileHeightHalf;
    protected float mapPartPixWidth;
    protected float mapPartPixHeight;
    protected int mapWidth, mapHeight;
    protected IsometricTiledMapRenderer renderer;
    protected OrthographicCamera camera;
    protected ExtendViewport viewport;
    protected TiledMapTileLayer gridLayer;
    protected TiledMapTileLayer selection;
    protected TiledMapTile selectionTile;
        protected Vector2 topTileCoords, bottomTileCoords, leftTileCoords, rightTileCoords,
        topTileWorldCoords, bottomTileWorldCoords, leftTileWorldCoords, rightTileWorldCoords, mapPartCenter;
    protected Viewport hudViewport = new ScreenViewport();
    protected Stage hudStage = new Stage(hudViewport);
    protected Skin hudSkin;
    protected HexaFrameBottom hexaFrameBottom;
    protected Button frameBottomHiddenButton = new Button();
    protected Button frameTopHiddenButton = new Button();
    protected boolean hudVisible = false;
    private HexaFrameTop hexaFrameTop;

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

        camera.update();

        renderer.setView(camera);
        renderer.render();

        hudStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        hudStage.draw();
    }

    @Override
    public final void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
        renderer.setView(camera);
        renderer.render();
        hudViewport.update(width, height, true);

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
    }

    /*
        Setup methods
     */
    private void setupMap() {
        manager.load("com/ragego/gui/maps/" + getMapToLoad() + ".tmx", TiledMap.class);
        manager.finishLoading();
        Gdx.app.log(TAG, "Map loaded");
        map = manager.get("com/ragego/gui/maps/" + getMapToLoad() + ".tmx");
        renderer = new IsometricTiledMapRenderer(map);
        camera = new OrthographicCamera();
        gridLayer = (TiledMapTileLayer) map.getLayers().get("grid");
        selection = (TiledMapTileLayer) map.getLayers().get("selection");
        final TiledMapTileSet toolTS = map.getTileSets().getTileSet("toolTS");
        selectionTile = toolTS.getTile(toolTS.getProperties().get("firstgid", Integer.class));
    }

    private void setupMapBoundsAndDimensions() {
        tileWidthHalf = map.getProperties().get("tilewidth", Integer.class) * 0.5f;
        tileHeightHalf = map.getProperties().get("tileheight", Integer.class) * 0.5f;
        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);

        //Offset between the actual coordinate system and the world coordinate system on the y-axis
        yOffset = tileHeightHalf;

        //Getting the coordinates of extremum tiles for screen sizing and centering
        topTileCoords = new Vector2(Float.parseFloat(map.getProperties().get("maxTopX", String.class)),
                Float.parseFloat(map.getProperties().get("maxTopY", String.class)));
        topTileWorldCoords = GuiUtils.isoToWorld(topTileCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);
        bottomTileCoords = new Vector2(Float.parseFloat(map.getProperties().get("maxBottomX", String.class)),
                Float.parseFloat(map.getProperties().get("maxBottomY", String.class)));
        bottomTileWorldCoords = GuiUtils.isoToWorld(bottomTileCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);
        leftTileCoords = new Vector2(Float.parseFloat(map.getProperties().get("maxLeftX", String.class)),
                Float.parseFloat(map.getProperties().get("maxLeftY", String.class)));
        leftTileWorldCoords = GuiUtils.isoToWorld(leftTileCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);
        rightTileCoords = new Vector2(Float.parseFloat(map.getProperties().get("maxRightX", String.class)),
                Float.parseFloat(map.getProperties().get("maxRightY", String.class)));
        rightTileWorldCoords = GuiUtils.isoToWorld(rightTileCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);

        //Size of the visible part of the map in world units + a padding of one tile
        mapPartPixWidth = rightTileWorldCoords.x - leftTileWorldCoords.x + tileWidthHalf * 2 + tileWidthHalf * 4;
        mapPartPixHeight = topTileWorldCoords.y - bottomTileWorldCoords.y + tileHeightHalf * 4 + tileHeightHalf * 4;

        //Determines the center coordinates of the map's visible part for camera centering
        mapPartCenter = new Vector2((rightTileWorldCoords.x + leftTileWorldCoords.x - 2 * tileWidthHalf) * 0.5f,
                (topTileWorldCoords.y + bottomTileWorldCoords.y - 4 * tileHeightHalf) * 0.5f);
    }

    private void setupCamera() {
        //Centers camera on map
        camera.translate(mapPartCenter.x, mapPartCenter.y);

        //Maximizes the map size on screen
        camera.viewportWidth = mapPartPixWidth;
        camera.viewportHeight = mapPartPixHeight;
        viewport = new ExtendViewport(mapPartPixWidth, mapPartPixHeight, camera);
    }

    /**
     * This function is called to configure a goban for this GameScreen.
     * In this function you should setup {@link com.ragego.engine.GameBoard} and {@link com.ragego.engine.Player}s for
     * this GoGame screen.
     *
     * @param goban The goban to setup.
     */
    protected abstract void setupGoban(Goban goban);

    /**
     * Setups the hud on GoGameScreen.
     */
    protected void setupHud() {
        manager.load("com/ragego/gui/hud/hud.json", Skin.class);
        manager.finishLoading();
        Gdx.app.log(TAG, "Hud loaded");
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

    private void setupHudShowButton() {
        frameBottomHiddenButton = new Button(hudSkin, "frame_bottom_hidden");
        frameTopHiddenButton = new Button(hudSkin, "frame_top_hidden");

        ClickListener hudButtonClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hudVisible = !hudVisible;
                showHud();
            }
        };
        frameBottomHiddenButton.addListener(hudButtonClickListener);
        frameTopHiddenButton.addListener(hudButtonClickListener);

        frameBottomHiddenButton.setPosition((hudViewport.getScreenWidth() - frameBottomHiddenButton.getWidth()) * 0.5f, 0);
        frameTopHiddenButton.setPosition((hudViewport.getScreenWidth() - frameTopHiddenButton.getWidth()) * 0.5f,
                hudViewport.getScreenHeight() - frameTopHiddenButton.getHeight());

        hudStage.addActor(frameBottomHiddenButton);
        hudStage.addActor(frameTopHiddenButton);
    }

    private void setupInputs() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexer.addProcessor(hudStage);
        gobanInputProcessor.addToMultiplexer(inputMultiplexer);
    }

    /*
        Visibility settings
     */
    void showHud() {
        if (hudVisible) {
            for (Actor actor : hudStage.getActors())
                if (actor != null)
                    actor.setVisible(true);
            frameBottomHiddenButton.setVisible(false);
            frameTopHiddenButton.setVisible(false);
        }
    }

    public void hideHud() {
        hideHud(false);
    }

    private void hideHud(boolean force) {
        if (hudVisible || force) {
            hudVisible = false;
            for (Actor actor : hudStage.getActors()) {
                if (actor != null)
                    actor.setVisible(false);
            }
            frameBottomHiddenButton.setVisible(true);
            frameTopHiddenButton.setVisible(true);
        }
    }

    /*
        Miscellaneous methods
     */
    public void closeScreen() {
        goban.stopGame();
        RageGoGame.goHome();
    }

    /**
     * Wait for a user input on Goban
     * Notice: this function is thread locking.
     *
     * @return The coordinates wanted or null if he want to pass his turn
     */
    public Vector2 waitForUserInputOnGoban() {
        Vector2 coordinates;
        synchronized (gobanInputProcessor) {
            while ((coordinates = gobanInputProcessor.popLastTouch()) == null && !goban.passTurn()) {
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
        return coordinates;
    }

    /**
     * Specify the map to load for this GoGame screen.
     */
    protected abstract String getMapToLoad();

    /*
        Getters and Setters
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    public TiledMap getMap() {
        return map;
    }
}