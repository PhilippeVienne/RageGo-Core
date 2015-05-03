package com.ragego.gui.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ragego.gui.RageGoGame;
import com.ragego.gui.menu.HexaBar;
import com.ragego.gui.menu.HexaBarButton;
import com.ragego.gui.objects.Goban;
import com.ragego.utils.GuiUtils;

/**
 * Manages the display of a generic Go Game Screen.
 */
public abstract class GoGameScreen extends ScreenAdapter {
    private static final String TAG = "GoGameScreen";
    private static final float HALF_TAP_SQUARE_SIZE = 20.0f;
    private static final float TAP_COUNT_INTERVAL = 0.4f;
    private static final float LONG_PRESS_DURATION = 1.1f;
    private static final float MAX_FLING_DELAY = 0.15f;
    protected final GobanInputProcessor gobanInputProcessor = new GobanInputProcessor();
    protected final InputMultiplexer inputMultiplexer = new InputMultiplexer();
    protected AssetManager manager;
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
    protected Viewport hudViewport;
    protected Stage hudStage;
    protected HexaBar hexaBar;
    protected Button hudButtonLeft = new Button();
    protected Button hudButtonRight = new Button();
    protected boolean hudVisible = false;
    private GestureDetector gestureDetector;

    @Override
    public final void show() {
        /*
            Map setup
         */
        manager = RageGoGame.getAssetManager();
        manager.load("com/ragego/gui/maps/" + getMapToLoad() + ".tmx", TiledMap.class);
        manager.finishLoading();
        Gdx.app.log(TAG, "Assets loaded");
        map = manager.get("com/ragego/gui/maps/" + getMapToLoad() + ".tmx");
        renderer = new IsometricTiledMapRenderer(map);
        camera = new OrthographicCamera();
        gridLayer = (TiledMapTileLayer) map.getLayers().get("grid");
        selection = (TiledMapTileLayer) map.getLayers().get("selection");
        final TiledMapTileSet toolTS = map.getTileSets().getTileSet("toolTS");
        selectionTile = toolTS.getTile(toolTS.getProperties().get("firstgid", Integer.class));

        tileWidthHalf = map.getProperties().get("tilewidth", Integer.class)*0.5f;
        tileHeightHalf = map.getProperties().get("tileheight", Integer.class)*0.5f;
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
        mapPartPixHeight = topTileWorldCoords.y - bottomTileWorldCoords.y  + tileHeightHalf * 4 + tileHeightHalf * 4;

        //Determines the center coordinates of the map's visible part for camera centering
        mapPartCenter = new Vector2((rightTileWorldCoords.x + leftTileWorldCoords.x - 2 * tileWidthHalf) * 0.5f,
            (topTileWorldCoords.y + bottomTileWorldCoords.y - 4 * tileHeightHalf) * 0.5f);

        //Centers camera on map
        camera.translate(mapPartCenter.x, mapPartCenter.y);

        //Maximizes the map size on screen
        camera.viewportWidth = mapPartPixWidth;
        camera.viewportHeight = mapPartPixHeight;
        viewport = new ExtendViewport(mapPartPixWidth, mapPartPixHeight, camera);

        goban = new Goban(this, map);
        setupGoban(goban);

        /*
            Input processors and HUD setup
        */
        hudViewport = new ScreenViewport();
        hudStage = new Stage(hudViewport);
        //hudStage.setDebugAll(true);

        gestureDetector = new GestureDetector(HALF_TAP_SQUARE_SIZE,
                TAP_COUNT_INTERVAL,
                LONG_PRESS_DURATION,
                MAX_FLING_DELAY,
                new GestureHandler());
        
        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexer.addProcessor(hudStage);
        inputMultiplexer.addProcessor(gestureDetector);
        inputMultiplexer.addProcessor(gobanInputProcessor);

        hexaBar = new HexaBar(hudViewport, hudStage);

        // Forward Button
        HexaBarButton forwardButton = new HexaBarButton(hexaBar, "com/ragego/gui/hexabar/forward_button_up.png",
                "com/ragego/gui/hexabar/forward_button_down.png", 1);
        forwardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        // Back Button
        HexaBarButton backButton = new HexaBarButton(hexaBar, "com/ragego/gui/hexabar/back_button_up.png",
                "com/ragego/gui/hexabar/back_button_down.png", 2);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        // Mark Button
        HexaBarButton markButton = new HexaBarButton(hexaBar, "com/ragego/gui/hexabar/mark_button_up.png",
                "com/ragego/gui/hexabar/mark_button_down.png", 3);
        markButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        // Pass Button
        HexaBarButton passButton = new HexaBarButton(hexaBar, "com/ragego/gui/hexabar/pass_button_up.png",
                "com/ragego/gui/hexabar/pass_button_down.png", 4);
        passButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goban.markTurnAsShouldBePassed();
            }
        });

        // Save Button
        HexaBarButton saveButton = new HexaBarButton(hexaBar, "com/ragego/gui/hexabar/save_button_up.png",
                "com/ragego/gui/hexabar/save_button_down.png", 5);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        // Inactive Buttons
        HexaBarButton inactiveButton1 = new HexaBarButton(hexaBar, "com/ragego/gui/hexabar/inactive_button.png", 6);
        HexaBarButton inactiveButton2 = new HexaBarButton(hexaBar, "com/ragego/gui/hexabar/inactive_button.png", 7);
        HexaBarButton inactiveButton3 = new HexaBarButton(hexaBar, "com/ragego/gui/hexabar/inactive_button.png", 8);
        HexaBarButton inactiveButton4 = new HexaBarButton(hexaBar, "com/ragego/gui/hexabar/inactive_button.png", 9);

        // Settings Button
        HexaBarButton settingsButton = new HexaBarButton(hexaBar, "com/ragego/gui/hexabar/settings_button_up.png",
                "com/ragego/gui/hexabar/settings_button_down.png", 10);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        // Return Button
        HexaBarButton returnButton = new HexaBarButton(hexaBar, "com/ragego/gui/hexabar/return_button_up.png",
                "com/ragego/gui/hexabar/return_button_down.png", 11);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goban.stopGame();
                RageGoGame.goHome();
            }
        });
        
        //Default behaviour is to hide the menu
        for (int i = 0 ; i < hudStage.getActors().size; i++) {
            hudStage.getActors().get(i).setVisible(false);
        }

        // HUD visibility button
        Button.ButtonStyle hudButtonStyle = new Button.ButtonStyle();
        hudButtonStyle.up = new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.classpath("com/ragego/gui/hexabar/hud_button_up.png"))));
        hudButtonStyle.down = new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.classpath("com/ragego/gui/hexabar/hud_button_down.png"))));
        hudButtonLeft = new Button(hudButtonStyle);
        hudButtonRight = new Button(hudButtonStyle);
        ClickListener hudButtonClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hudVisible = !hudVisible;
                showHud();
            }
        };
        hudButtonLeft.addListener(hudButtonClickListener);
        hudButtonLeft.setPosition(0, 0);
        hudButtonRight.addListener(hudButtonClickListener);
        hudButtonRight.setPosition(hudViewport.getScreenWidth() - hudButtonRight.getWidth(), 0);
        hudStage.addActor(hudButtonLeft);
        hudStage.addActor(hudButtonRight);
    }


    private void showHud() {
        if (hudVisible) {
            for (Actor actor : hudStage.getActors())
                if (actor != null)
                    actor.setVisible(true);
            hudButtonLeft.setVisible(false);
            hudButtonRight.setVisible(false);
        }
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
     * Specify the map to load for this GoGame screen.
     */
    protected abstract String getMapToLoad();

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
        hexaBar.update(hudViewport);
        hudButtonLeft.setPosition(0, 0);
        hudButtonRight.setPosition(hudViewport.getScreenWidth() - hudButtonRight.getWidth(), 0);
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
        dispose();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        hudStage.dispose();
    }

    public TiledMap getMap() {
        return map;
    }

    /**
     * Wait for a user input on Goban
     *
     * @return The coordinates
     */
    public Vector2 waitForUserInputOnGoban() {
        Vector2 coordinates;
        synchronized (gobanInputProcessor) {
            while ((coordinates = gobanInputProcessor.popLastTouch()) == null && !goban.passTurn()) {
                try {
                    Thread.sleep(0, 500);
                    gobanInputProcessor.wait(5);
                } catch (InterruptedException e) {
                    Gdx.app.debug("Threads", "GameEngine thread has been closed");
                }
            }
        }
        if (goban.passTurn()) // Null return means user pass his turn
            return null;
        return coordinates;
    }

    private void hideHud() {
        if (hudVisible) {
            hudVisible = false;
            for (Actor actor : hudStage.getActors()) {
                if (actor != null)
                    actor.setVisible(false);
            }
            hudButtonLeft.setVisible(true);
            hudButtonRight.setVisible(true);
        }
    }

    @SuppressWarnings("unused")
    public class GobanInputProcessor implements InputProcessor {

        private Vector2 lastTouch = null;
        private TiledMapTileLayer.Cell selectionCell;

        public Vector2 popLastTouch() {
            Vector2 result = lastTouch;
            lastTouch = null;
            return result;
        }

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                goban.stopGame();
                RageGoGame.goHome();
            }
            return false;
        }

        @Override
        public boolean keyTyped(char character) { // Only if you want to listen characters
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            hideHud();
            if (button == Input.Buttons.LEFT) {
                Vector3 tempCoords = new Vector3(screenX, screenY, 0);
                Vector3 worldCoords = camera.unproject(tempCoords);

                Vector2 touch = GuiUtils.worldToIsoLeft(worldCoords, tileWidthHalf, tileHeightHalf, yOffset);
                showCrossOn(touch);
                return false;
            } else {
                return true;
            }
        }

        private void showCrossOn(Vector2 position) {
            hideCross();
            Vector2 positionCopy = position.cpy();
            if (goban.isValidOnGoban(GuiUtils.isoLeftToIsoTop(positionCopy, mapHeight))) {
                selectionCell = new TiledMapTileLayer.Cell();
                selectionCell.setTile(selectionTile);
                selection.setCell((int) position.x, (int) position.y, selectionCell);
            }
        }

        private void hideCross() {
            selectionCell = null;
            for (int x = 0; x < selection.getWidth(); x++)
                for (int y = 0; y < selection.getHeight(); y++)
                    if (selection.getCell(x, y) != null)
                        selection.getCell(x, y).setTile(null);
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (button == Input.Buttons.LEFT) {
                Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
                hideCross();
                lastTouch = GuiUtils.worldToIsoTop(worldCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);
                return false;
            } else {
                return true;
            }
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (selectionCell != null) {
                Vector3 tempCoords = new Vector3(screenX, screenY, 0);
                Vector3 worldCoords = camera.unproject(tempCoords);

                Vector2 touch = GuiUtils.worldToIsoLeft(worldCoords, tileWidthHalf, tileHeightHalf, yOffset);
                showCrossOn(touch);
                return false;
            } else {
                return true;
            }
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }
    }

    //TODO : improve zoom implementation when Hash problem solved
    public class GestureHandler implements GestureDetector.GestureListener {
        float initialScale = 1;

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            return false;
        }

        @Override
        public boolean longPress(float x, float y) {
            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            return false;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            initialScale = camera.zoom;
            float ratio = initialDistance / distance;
            camera.zoom = initialScale * ratio;
            return false;
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            return false;
        }

    }
}
