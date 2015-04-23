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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.ragego.gui.RageGoGame;
import com.ragego.gui.objects.Goban;
import com.ragego.utils.GuiUtils;

/**
 * Manages the display of a generic Go Game Screen.
 */
public class GoGameScreen extends ScreenAdapter {
    private static final String TAG = "GoGameScreen";
    protected final MyGestureListener gesture = new MyGestureListener();
    protected final InputMultiplexer inputMultiplexer = new InputMultiplexer(gesture);
    protected AssetManager manager;
    protected TiledMap map;
    protected Goban goban;
    protected float mapUnit, yOffset, tileWidthHalf, tileHeightHalf, mapPixWidth, mapPixHeight;
    protected int mapWidth, mapHeight;
    protected IsometricTiledMapRenderer renderer;
    protected OrthographicCamera camera;
    protected ExtendViewport viewport;
    protected Stage stage;
    protected TiledMapTileLayer gridLayer;
    protected TiledMapTileLayer selection;
    protected TiledMapTile selectionTile;

    @Override
    public void show() {
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
        manager.load("com/ragego/gui/maps/Goban.tmx", TiledMap.class);
        manager.finishLoading();
        Gdx.app.log(TAG, "Assets loaded");
        map = manager.get("com/ragego/gui/maps/Goban.tmx");

        renderer = new IsometricTiledMapRenderer(map);
        camera = new OrthographicCamera();
        gridLayer = (TiledMapTileLayer)map.getLayers().get("grid");
        selection = (TiledMapTileLayer) map.getLayers().get("selection");
        final TiledMapTileSet toolTS = map.getTileSets().getTileSet("toolTS");
        selectionTile = toolTS.getTile(toolTS.getProperties().get("firstgid", Integer.class));

        //Map size in world units (+1 tile's height to compensate the 3d effect)
        tileWidthHalf = map.getProperties().get("tilewidth", Integer.class)*0.5f;
        tileHeightHalf = map.getProperties().get("tileheight", Integer.class)*0.5f;

        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);

        mapPixWidth = (float)mapWidth * tileWidthHalf * 2;
        mapPixHeight = (float)mapHeight * tileHeightHalf * 2;

        //Map unit (useful for screen/map coordinates conversion)
        mapUnit = (float)(Math.sqrt(Math.pow(tileWidthHalf, 2) + Math.pow(tileHeightHalf, 2)));

        //Active Tile Layer Offset on y-axis (TODO make it map.tmx dependent)
        yOffset = tileHeightHalf;

        //Centers camera on map
        camera.translate(mapPixWidth * 0.5f, 0);

        //Maximizes the map size on screen
        viewport = new ExtendViewport(mapPixWidth, mapPixHeight + tileHeightHalf * 2, camera);

        /*
            Goban setup
         */

        goban = new Goban(this, map);

        /*
            Interaction components setup
         */
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        renderer.setView(camera);
        goban.updateStones();
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
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
    public class MyGestureListener implements InputProcessor {

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
            Vector3 tempCoords = new Vector3(screenX, screenY, 0);
            Vector3 worldCoords = camera.unproject(tempCoords);

            Vector2 touch = GuiUtils.worldToIsoLeft(worldCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);
            showCrossOn(touch);
            return false;
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
            for (int x = 0; x < selection.getWidth(); x++)
                for (int y = 0; y < selection.getHeight(); y++)
                    if (selection.getCell(x, y) != null)
                        selection.getCell(x, y).setTile(null);
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            Vector3 tempCoords = new Vector3(screenX, screenY, 0);
            Vector3 worldCoords = camera.unproject(tempCoords);
            hideCross();
            lastTouch = GuiUtils.worldToIsoTop(worldCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            Vector3 tempCoords = new Vector3(screenX, screenY, 0);
            Vector3 worldCoords = camera.unproject(tempCoords);

            Vector2 touch = GuiUtils.worldToIsoLeft(worldCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);
            showCrossOn(touch);
            return false;
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
