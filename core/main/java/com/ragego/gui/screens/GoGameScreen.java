package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class GoGameScreen extends ScreenAdapter {
    private TiledMap map;
    private IsometricTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private ExtendViewport viewport;

    @Override
    public void show() {
        map = new TmxMapLoader().load("android/assets/maps/Goban.tmx");
        renderer = new IsometricTiledMapRenderer(map);
        camera = new OrthographicCamera();

        //Map size in world units (+1 tile's height to compensate the 3d effect)
        float mapWidth = (float)map.getProperties().get("width", Integer.class)* map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = (float)(map.getProperties().get("height", Integer.class) + 1) * map.getProperties().get("tileheight", Integer.class);

        //Centers camera on map
        camera.translate(mapWidth*0.5f, 0);

        //Maximizes the map size on screen
        viewport = new ExtendViewport(mapWidth, mapHeight, camera);

        /*Sets the maps colors (WIP, create a texture color changing method and apply it here.
        Check http://stackoverflow.com/questions/24034352/libgdx-change-color-of-texture-at-runtime for help

        TiledMapTileSet gridElements = map.getTileSets().getTileSet("gridTS");
        Color gridColor = new Color(Color.BLACK);

        for (int i=0; i<gridElements.; i++) {
            gridElements.getTile(i).getTextureRegion().getTexture().getTextureData().
        }
        System.out.println(gridElements.size());
        */
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
}
