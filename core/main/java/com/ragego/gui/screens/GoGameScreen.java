package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.ragego.gui.objects.Goban;
import com.ragego.utils.GuiUtils;

/**
 * Manages the display of a generic Go Game Screen.
 */
public class GoGameScreen extends ScreenAdapter {
    private static final String TAG = "GoGameScreen";

    private TiledMap map;
    private Goban goban;
    private float mapUnit, yOffset, tileWidthHalf, tileHeightHalf, mapPixWidth, mapPixHeight;
    private int mapWidth, mapHeight;
    private IsometricTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private Stage stage;

    private final MyGestureListener gesture = new MyGestureListener();
    private InputMultiplexer myInputMultiplexer;

    TiledMapTileLayer gridLayer;

    @Override
    public void show() {
        /*
            Map setup
         */
        map = new TmxMapLoader(new FileHandleResolver() {
            @Override
            public FileHandle resolve(String fileName) {
                return Gdx.files.classpath(fileName);
            }
        }).load("com/ragego/gui/maps/Goban.tmx");
        renderer = new IsometricTiledMapRenderer(map);
        camera = new OrthographicCamera();
        gridLayer = (TiledMapTileLayer)map.getLayers().get("grid");

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

        TiledMapTileLayer stoneLayer = (TiledMapTileLayer) map.getLayers().get("stones");
        TiledMapTile blackStone = map.getTileSets().getTileSet("stoneTS").getTile(0);
        TiledMapTileLayer.Cell cell = stoneLayer.getCell(10, 10);
        //cell.setTile(null);

        /*Sets the maps colors (WIP, create a texture color changing method and apply it here.
        Check http://stackoverflow.com/questions/24034352/libgdx-change-color-of-texture-at-runtime for help

        TiledMapTileSet gridElements = map.getTileSets().getTileSet("gridTS");
        Color gridColor = new Color(Color.BLACK);

        for (int i=0; i<gridElements.size()-1; i++) {
            gridElements.getTile(i).getTextureRegion().getTexture().getTextureData().
        }
        System.out.println(gridElements.size());
        */


        /*
            Goban setup
         */

        goban = new Goban(this, map);

        /*
            Interaction components setup
         */
        Gdx.input.setInputProcessor(new GestureDetector(gesture));

        goban.startGame();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        map.dispose();
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
                    gesture.wait(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return coordinates;
    }

    public class MyGestureListener implements GestureDetector.GestureListener {

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            Vector3 tempCoords = new Vector3(x,y,0);
            Vector3 worldCoords = camera.unproject(tempCoords);

            lastTouch = GuiUtils.worldToIso(worldCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset);
            return false;
        }

        private Vector2 lastTouch = null;

        public Vector2 popLastTouch() {
            Vector2 result = lastTouch;
            lastTouch = null;
            return result;
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
            //Gdx.app.log("Text", "panstop");
            return false;
        }


        @Override
        public boolean zoom (float originalDistance, float currentDistance){
            return false;
        }

        @Override
        public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer){
            return false;
        }
    }
}
