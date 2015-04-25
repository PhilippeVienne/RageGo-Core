package com.ragego.gui.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.ragego.gui.RageGoGame;
import com.ragego.gui.objects.Goban;
import com.ragego.utils.GuiUtils;

/**
 * Manages the display of a generic Go Game Screen.
 */
public abstract class GoGameScreen extends ScreenAdapter {
    private static final String TAG = "GoGameScreen";
    protected final GobanInputListener gesture = new GobanInputListener();
    protected final InputMultiplexer inputMultiplexer = new InputMultiplexer(gesture);
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

    @Override
    public final void show() {
        /*
            Map setup
         */
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
        viewport = new ExtendViewport(mapPartPixWidth, mapPartPixHeight, camera);

        goban = new Goban(this, map);
        setupGoban(goban);

        /*
            Interaction components setup
         */
        Gdx.input.setInputProcessor(inputMultiplexer);
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
    }

    @Override
    public final void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
        renderer.setView(camera);
        renderer.render();
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
        manager.dispose();
        renderer.dispose();
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
        synchronized (gesture) {
            while ((coordinates = gesture.popLastTouch()) == null) {
                try {
                    Thread.sleep(0, 500);
                    gesture.wait(5);
                } catch (InterruptedException e) {
                    Gdx.app.debug("Threads", "GameEngine thread has been closed");
                }
            }
        }
        return coordinates;
    }

    @SuppressWarnings("unused")
    public class GobanInputListener implements InputProcessor {

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
}
