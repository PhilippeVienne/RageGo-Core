package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.ragego.utils.GuiUtils;

/**
 * Manages the display of a generic Go Game Screen.
 */
public class GoGameScreen extends ScreenAdapter {
    private static final String TAG = "GoGameScreen";

    private TiledMap map;
    private float mapUnit, yOffset, tileWidthHalf, tileHeightHalf;
    private IsometricTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private Stage stage;

    GestureDetector gesture;
    InputMultiplexer myInputMultiplexer;

    private float mapWidth, mapHeight;

    TiledMapTileLayer grid_layer;

    @Override
    public void show() {
        /*
            Map setup
         */
        map = new TmxMapLoader().load("android/assets/maps/Goban.tmx");
        renderer = new IsometricTiledMapRenderer(map);
        camera = new OrthographicCamera();
        grid_layer = (TiledMapTileLayer)map.getLayers().get("grid");

        //Map size in world units (+1 tile's height to compensate the 3d effect)
        tileWidthHalf = map.getProperties().get("tilewidth", Integer.class)*0.5f;
        tileHeightHalf = map.getProperties().get("tileheight", Integer.class)*0.5f;
        System.out.println("tileWidthHalf = " + tileWidthHalf + " & " + "tileHeightHalf = " + tileHeightHalf);

        mapWidth = (float)map.getProperties().get("width", Integer.class)* map.getProperties().get("tilewidth", Integer.class);
        mapHeight = (float)map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);

        //Map unit (useful for screen/map coordinates conversion)
        mapUnit = (float)(Math.sqrt(Math.pow(map.getProperties().get("tilewidth", Integer.class) * 0.5d, 2)
                + Math.pow(map.getProperties().get("tileheight", Integer.class) * 0.5d, 2)));

        //Active Tile Layer Offset on y-axis (TODO make it map.tmx dependent)
        yOffset = map.getProperties().get("tileheight", Integer.class) * 0.5f;

        //Centers camera on map
        camera.translate(mapWidth * 0.5f, 0);

        //Maximizes the map size on screen
        viewport = new ExtendViewport(mapWidth, mapHeight + map.getProperties().get("tileheight", Integer.class), camera);

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
            Interaction components setup
         */
        gesture = new GestureDetector(new MyGestureListener());
        Gdx.input.setInputProcessor(gesture);
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
    public class MyGestureListener implements GestureDetector.GestureListener {

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            Vector3 tempCoords = new Vector3(x,y,0);
            Vector3 worldCoords = camera.unproject(tempCoords);
            Vector2 isoCoords = GuiUtils.worldToIso(worldCoords, mapUnit, yOffset);

            System.out.println("Screen coordinates : "
                    + "X: " + x + " Y: " + y);

            System.out.println("World coordinates : "
                    + "X: " + worldCoords.x + " Y: " + worldCoords.y);

            System.out.println("Isometric coordinates : "
                    + "X: " + isoCoords.x + " Y: " + isoCoords.y);

            System.out.println("******************************");

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
