package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ragego.gui.ImprovedGestureDetector;
import com.ragego.utils.GuiUtils;

/**
 * Listen mouse and touch events for a {@link GoGameScreen}
 */
public class GoGameScreenMouseTouchListener implements InputProcessor {

    private static final int MAX_FINGERS_ON_SCREEN = 5;
    private static final float MIN_ZOOM = 0.25f;
    private static final float MAX_ZOOM = 1.2f;
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
    private Vector2 panningLastOrigin;
    private long noPlacingUntil = 0;
    private long panningLastTimeRefresh = 0;

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
        multiplexer.addProcessor(size, new ImprovedGestureDetector(new GoGameScreenMouseTouchListener.Gestures()));
        multiplexer.addProcessor(size + 1, this);
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
        Gdx.app.log("IHM", "Touch down on " + screenX + "," + screenY + " with " + pointer);
        if (screen.hudVisible) {
            screen.hideHud();
        }
        pointers[pointer] = new Vector2(screenX, screenY);
        updateCurrentAction();
        resetCounters();
        if (placingStone) {
            Vector3 tempCoords = new Vector3(screenX, screenY, 0);
            Vector3 worldCoords = screen.getCamera().unproject(tempCoords);
            Vector2 touch = GuiUtils.worldToIsoLeft(worldCoords, screen.tileWidthHalf, screen.tileHeightHalf, screen.yOffset);
            showCrossOn(touch);
        } else {
            System.out.println("Panning3");
            hideCross();
        }
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
     * 1 is a stone placement, 2 a panning. On a computer with a mouse, it always be the 1 value.
     */
    private void updateCurrentAction() {
        switch (getActivePointersCount()) {
            case 1:
                placingStone = true;
                panning = false;
                break;
            case 2:
                System.out.println("Panning2");
                panning = true;
                placingStone = false;
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        pointers[pointer] = null;
        if (getActivePointersCount() == 0) {
            if (placingStone && noPlacingUntil < System.currentTimeMillis()) {
                Vector3 worldCoords = screen.getCamera().unproject(new Vector3(screenX, screenY, 0));
                hideCross();
                lastTouch = GuiUtils.worldToIsoTop(worldCoords, screen.tileWidthHalf, screen.tileHeightHalf, screen.mapHeight, screen.yOffset);
                placingStone = false;
                return true;
            } else if (panning) { // Make the last move
                panningLastOrigin = null;
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
        if (pointer != 0 && (panning || placingStone)) return true;
        else if (!panning && !placingStone) return false;
        // Update the stored pointer
        if (pointers[pointer] != null) {
            pointers[pointer].set(screenX, screenY);
        } else {
            pointers[pointer] = new Vector2(screenX, screenY);
        }

        // Update or just do actions
        if (placingStone && noPlacingUntil < System.currentTimeMillis()) {
            Vector3 tempCoords = new Vector3(screenX, screenY, 0);
            Vector3 worldCoords = screen.getCamera().unproject(tempCoords);

            Vector2 touch = GuiUtils.worldToIsoLeft(worldCoords, screen.tileWidthHalf, screen.tileHeightHalf, screen.yOffset);
            showCrossOn(touch);
            return true;
        } else if (panning) {
            if (panningLastOrigin != null) {
                Vector2 delta = panningLastOrigin.add(-screenX, -screenY).scl(-1);
                Vector2 newPos = new Vector2(screen.camera.position.x, screen.camera.position.y).add(delta);
                if (screen.renderer.getViewBounds().contains(newPos))
                    screen.camera.translate(delta);
                panningLastOrigin.x = screenX;
                panningLastOrigin.y = screenY;
            } else {
                System.out.println("Update");
                panningLastOrigin = pointers[pointer].cpy();
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

    private class Gestures extends GestureDetector.GestureAdapter {

        @Override
        public boolean zoom(float initialDistance, float distance) {
            if (initialDistance < 250 && distance < 250) return false;
            noPlacingUntil = System.currentTimeMillis() + 500;
            initialZoom = screen.camera.zoom;
            float ratio = initialDistance / distance;
            if (initialZoom * ratio < MIN_ZOOM) {
                screen.camera.zoom = MIN_ZOOM;
            } else if (initialZoom * ratio > MAX_ZOOM) {
                screen.camera.zoom = MAX_ZOOM;
            } else {
                screen.camera.zoom = initialZoom * ratio;
            }
            return true;
        }

    }

}
