package com.ragego.utils;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;

/**
 * Defines who to get the bounds of our map to display.
 */
public class MapUtils {

    public static final String FIRSTGID = "firstgid";
    public static final String TOOLS_TILESET = "toolTS";
    public final static String MAX_TOP_X_KEY = "maxTopX";
    public final static String MAX_TOP_Y_KEY = "maxTopY";
    public final static String MAX_BOTTOM_X_KEY = "maxBottomX";
    public final static String MAX_BOTTOM_Y_KEY = "maxBottomY";
    public final static String MAX_LEFT_X_KEY = "maxLeftX";
    public final static String MAX_LEFT_Y_KEY = "maxLeftY";
    public final static String MAX_RIGHT_X_KEY = "maxRightX";
    public final static String MAX_RIGHT_Y_KEY = "maxRightY";
    private static final String TILE_HEIGHT_KEY = "tileheight";
    private static final String TILE_WIDTH_KEY = "tilewidth";
    private static final String WIDTH_KEY = "width";
    private static final String HEIGHT_KEY = "height";
    private static final float HALF = 0.25f;
    private final TiledMap tiledMap;

    public MapUtils(TiledMap map) {
        this.tiledMap = map;
    }

    public Integer getMapPropertyAsInteger(String property) {
        try {
            return tiledMap.getProperties().get(property, Integer.class);
        } catch (Exception e) {
            return Integer.parseInt(tiledMap.getProperties().get(property, String.class));
        }
    }

    public Float getMapPropertyAsFloat(String property) {
        try {
            return tiledMap.getProperties().get(property, Float.class);
        } catch (Exception e) {
            return (float) getMapPropertyAsInteger(property);
        }
    }

    public Vector2 getVector2FromProperties(String propertyForX, String propertyForY) {
        return new Vector2(getMapPropertyAsFloat(propertyForX), getMapPropertyAsFloat(propertyForY));
    }

    public Vector2 getWorldCoordinated(String propertyForX, String propertyForY) {
        return GuiUtils.isoToWorld(
                getVector2FromProperties(propertyForX, propertyForY),
                getTileWidthHalf(),
                getTileHeightHalf(),
                getMapWidth(),
                getTileHeightHalf());
    }

    public Integer getMapHeight() {
        return getMapPropertyAsInteger(HEIGHT_KEY);
    }

    public Integer getMapWidth() {
        return getMapPropertyAsInteger(WIDTH_KEY);
    }

    public float getTileHeightHalf() {
        return getMapPropertyAsFloat(TILE_WIDTH_KEY) * HALF;
    }

    public float getTileWidthHalf() {
        return getMapPropertyAsFloat(TILE_HEIGHT_KEY) * HALF;
    }

    public Vector2 getTopBound() {
        return getWorldCoordinated(MAX_TOP_X_KEY, MAX_TOP_Y_KEY);
    }

    public Vector2 getDownBound() {
        return getWorldCoordinated(MAX_BOTTOM_X_KEY, MAX_BOTTOM_Y_KEY);
    }

    public Vector2 getLeftBound() {
        return getWorldCoordinated(MAX_LEFT_X_KEY, MAX_LEFT_Y_KEY);
    }

    public Vector2 getRightBound() {
        return getWorldCoordinated(MAX_RIGHT_X_KEY, MAX_RIGHT_Y_KEY);
    }

    public TiledMapTileLayer getLayer(String key) {
        return (TiledMapTileLayer) tiledMap.getLayers().get(key);
    }

    public TiledMapTileSet getTileSet(String name) {
        return tiledMap.getTileSets().getTileSet(name);
    }

    public Integer getIntegerPropertyOnTileset(String tilesetName, String property) {
        return getTileSet(tilesetName).getProperties().get(property, Integer.class);
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }
}
