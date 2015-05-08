package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
    private Vector2[] pointers = new Vector2[20];
    private Vector2[] zoomStartPoints = new Vector2[2];
    private float zoom = 0f;
    private float initialZoom = 1f;
    private float deltaX;
    private float deltaY;

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
        if (screen.hudVisible) {
            screen.hideHud();
        }
        pointers[pointer] = new Vector2(screenX, screenY);
        updateCurrentAction();
        resetCounters();
        return false;
    }

    private void resetCounters() {
        zoom = 0f;
        initialZoom = screen.camera.zoom;
        deltaX = 0f;
        deltaY = 0f;
    }

    /**
     * Determine what gesture we have.
     * 1 is a stone placement, 2 is a zoom and 3 a panning. On a computer with a mouse, it always be the 1 value.
     */
    private void updateCurrentAction() {
        switch (getActivePointersCount()) {
            case 1:
                placingStone = true;
                zooming = false;
                panning = false;
                break;
            case 2:
                zooming = true;
                panning = false;
                placingStone = false;
                break;
            case 3:
                panning = true;
                zooming = false;
                placingStone = false;
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        pointers[pointer] = null;
        if (getActivePointersCount() == 0) {
            Gdx.app.log("Oh", "We have no more pointer on screen !");
            if (placingStone) {
                Vector3 worldCoords = screen.getCamera().unproject(new Vector3(screenX, screenY, 0));
                hideCross();
                lastTouch = GuiUtils.worldToIsoTop(worldCoords, screen.tileWidthHalf, screen.tileHeightHalf, screen.mapHeight, screen.yOffset);
                placingStone = false;
                return true;
            } else if (panning) { // Make the last move

            } else if (zooming) { // Make the last zoom
                zoomStartPoints = new Vector2[2];
            }
        }
        return false;
    }

    private int getActivePointersCount() {
        int count = 0;
        for (int i = 0; i < MAX_FINGERS_ON_SCREEN; i++) {
            if (pointers[i] != null) count++;
        }
        return count;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Update the stored pointer
        if (pointers[pointer] != null) {
            pointers[pointer].set(screenX, screenY);
        } else {
            pointers[pointer] = new Vector2(screenX, screenY);
        }

        // Determine if we are always doing the same action
        updateCurrentAction();

        // Update or just do actions
        if (placingStone) {
            Vector3 tempCoords = new Vector3(screenX, screenY, 0);
            Vector3 worldCoords = screen.getCamera().unproject(tempCoords);

            Vector2 touch = GuiUtils.worldToIsoLeft(worldCoords, screen.tileWidthHalf, screen.tileHeightHalf, screen.yOffset);
            showCrossOn(touch);
            return true;
        } else if (zooming) {
            Vector2 p1 = null, p2 = null;
            for (int i = 0; i < pointers.length && (p1 == null || p2 == null); i++) {
                if (pointers[i] != null) {
                    if (p1 == null) {
                        p1 = pointers[i];
                    } else {
                        p2 = pointers[i];
                    }
                }
            }
            if (p1 == null || p2 == null) {
                return false;
            }
            final float finalDist = p1.dst(p2);
            if (zoomStartPoints[0] == null || zoomStartPoints[1] == null) {
                zoomStartPoints[0] = p1;
                zoomStartPoints[1] = p2;
            } else {
                final float startDist = zoomStartPoints[0].dst(zoomStartPoints[1]);
                final float factor = (startDist / finalDist) * 0.5f;
                float zoom = initialZoom * factor;
                if (zoom < 0.25f)
                    zoom = 0.25f;
                else if (zoom > 1.0f)
                    zoom = 1.0f;
                screen.getCamera().zoom = zoom;
            }
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
