package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ragego.utils.GuiUtils;

/**
 * Listen mouse and touch events for a {@link GoGameScreen}
 */
public class GoGameScreenMouseTouchListener implements InputProcessor {

    private static final int MAX_FINGERS_ON_SCREEN = 5;
    private boolean placingStone = false;
    private boolean zooming = false;
    private boolean panning = false;
    private GoGameScreen screen;
    private Vector2 lastTouch = null;
    private TiledMapTileLayer.Cell selectionCell;
    private boolean activeToPutStones = true;
    private Array<Vector2> pointers = new Array<Vector2>(5);

    public GoGameScreenMouseTouchListener(GoGameScreen screen) {
        if (screen == null)
            throw new NullPointerException("Screen is null");
        this.screen = screen;
    }

    public boolean isActiveToPutStones() {
        return activeToPutStones;
    }

    public void setActiveToPutStones(boolean activeToPutStones) {
        this.activeToPutStones = activeToPutStones;
    }

    /**
     * Get the last value stored for user input on Goban.
     *
     * @return The cordinate on Tiled. null if no data.
     */
    public Vector2 popLastTouch() {
        Vector2 result = lastTouch;
        lastTouch = null;
        return result;
    }

    /**
     * Add the listeners to the {@link InputMultiplexer}. By order, the input will be first processed by gesture and
     * after by the basic input listener.
     *
     * @param multiplexer The multiplexer where you want to add.
     */
    public void addToMultiplexer(InputMultiplexer multiplexer) {
        final int size = multiplexer.getProcessors().size;
        multiplexer.addProcessor(size, this);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) { // Only if you want to listen characters
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        pointers.set(pointer, new Vector2(screenX, screenY));
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        pointers.removeIndex(pointer);
        if (getActivePointersCount() == 0) {
            Gdx.app.log("Oh", "We have no more pointer on screen !");
            if (placingStone) {
                Vector3 worldCoords = screen.getCamera().unproject(new Vector3(screenX, screenY, 0));
                hideCross();
                lastTouch = GuiUtils.worldToIsoTop(worldCoords, screen.tileWidthHalf, screen.tileHeightHalf, screen.mapHeight, screen.yOffset);
                placingStone = false;
                return true;
            }
        }
        return false;
    }

    private int getActivePointersCount() {
        int count = 0;
        for (int i = 0; i < pointers.size; i++) {
            if (pointers.get(i) != null) count++;
        }
        return count;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        pointers.set(pointer, new Vector2(screenX, screenY));
        if (!placingStone && getActivePointersCount() == 1) {
            placingStone = true;
            zooming = false;
            panning = false;
        } else if (placingStone && getActivePointersCount() != 1) {
            placingStone = false;
        }
        if (placingStone) {
            Vector3 tempCoords = new Vector3(screenX, screenY, 0);
            Vector3 worldCoords = screen.getCamera().unproject(tempCoords);

            Vector2 touch = GuiUtils.worldToIsoLeft(worldCoords, screen.tileWidthHalf, screen.tileHeightHalf, screen.yOffset);
            showCrossOn(touch);
            return true;
        } else {

            return false;
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

    /**
     * Count number of fingers there is on screen.
     *
     * @return Number of fingers >=0 and <=5
     */
    private int getFingersOnScreen() {
        int activeTouch = 0;
        for (int i = 0; i < MAX_FINGERS_ON_SCREEN; i++) {
            if (Gdx.input.isTouched(i)) activeTouch++;
        }
        return activeTouch;
    }

    /**
     * Display the selection helper cross on a position
     *
     * @param position The position to display it.
     */
    private void showCrossOn(final Vector2 position) {
        hideCross();
        Vector2 positionCopy = position.cpy();
        if (screen.goban.isValidOnGoban(GuiUtils.isoLeftToIsoTop(positionCopy, screen.mapHeight))) {
            selectionCell = new TiledMapTileLayer.Cell();
            selectionCell.setTile(screen.selectionTile);
            screen.selection.setCell((int) position.x, (int) position.y, selectionCell);
        }
    }

    /**
     * Hide the selection cross.
     */
    private void hideCross() {
        selectionCell = null;
        for (int x = 0; x < screen.selection.getWidth(); x++)
            for (int y = 0; y < screen.selection.getHeight(); y++)
                if (screen.selection.getCell(x, y) != null)
                    screen.selection.getCell(x, y).setTile(null);
    }
}
