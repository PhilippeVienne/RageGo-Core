package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.ragego.gui.FirewalledGestureDetector;
import com.ragego.utils.GuiUtils;

/**
 * Listen mouse and touch events for a {@link GoGameScreen}
 */
public class GoGameScreenMouseTouchListener implements InputProcessor {

    /**
     * Max number of fingers we consider on the screen
     */
    private static final int MAX_FINGERS_ON_SCREEN = 5;
    /**
     * Min zoom possible with the camera
     */
    private static final float MIN_ZOOM = 0.5f;
    /**
     * Max zoom possible with the camera
     */
    private static final float MAX_ZOOM = 1.2f;
    /**
     * Flag to determine if we are placing stones on the board.
     */
    private boolean placingStone = false;
    /**
     * Flag to determine if we are panning the screen.
     */
    private boolean panning = false;
    /**
     * The attahced screen for this listener.
     * This is useful to grab camera settings and goban information.
     */
    private GoGameScreen screen;
    /**
     * Last point touched which can be considered as a point where the user wants to put a stone.
     */
    private Vector2 lastTouch = null;
    /**
     * Cell which contains the selection cross for the placing stone listener
     */
    private TiledMapTileLayer.Cell selectionCell;
    /**
     * Flag to describe if we can put stones on the board.
     * This is updated with {@link #setActiveToPutStones(boolean)}
     */
    private boolean activeToPutStones = true;
    /**
     * Last pointers given by the library
     */
    private Vector2[] pointers = new Vector2[20];
    /**
     * Zoom value when the user start to zoom or zoom out.
     */
    private float initialZoom = 1f;
    /**
     * Last position used for panning computing
     */
    private Vector2 panningLastOrigin;
    /**
     * Counter to not place stone just after zoomed or panned
     */
    private long noPlacingUntil = 0;
    /**
     * Task used to pan while a key is down
     */
    private KeyPanning panningTask;
    /**
     * Thread used to call the {@link #panningTask}.
     * This thread should always be running.
     */
    private final Thread panningThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                if (panningTask != null)
                    panningTask.run();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    panningThread.start();
                }
            }
        }
    }, "PanningThread");

    {
        panningThread.start();
    }

    public GoGameScreenMouseTouchListener(GoGameScreen screen) {
        if (screen == null)
            throw new NullPointerException("Screen is null");
        this.screen = screen;
    }

    public boolean isActiveToPutStones() {
        return activeToPutStones;
    }

    /**
     * Determine if we are able to put stones on the board.
     *
     * @param activeToPutStones true if we should listen for stones
     */
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
        multiplexer.addProcessor(size, new FirewalledGestureDetector(new GoGameScreenMouseTouchListener.Gestures()));
        multiplexer.addProcessor(size + 1, this);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.RIGHT:
            case Input.Keys.LEFT:
            case Input.Keys.UP:
            case Input.Keys.DOWN:
                if (panningTask == null) {
                    panningTask = new KeyPanning(keycode);
                    return true;
                } else {
                    panningTask.keyCode = keycode;
                    return true;
                }
            default:
                return false;
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.RIGHT:
            case Input.Keys.LEFT:
            case Input.Keys.UP:
            case Input.Keys.DOWN:
                if (panningTask != null) {
                    if (panningTask.keyCode == keycode) {
                        panningTask = null;
                        return true;
                    }
                }
            default:
                return false;
        }
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
            hideCross();
        }
        return false;
    }

    /**
     * Reset all temp values to defaults
     */
    private void resetCounters() {
        initialZoom = screen.camera.zoom;
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
                placingStone = false;
                panning = false;
                break;
            case 3:
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

    /**
     * Count pointer currently stored for this listener.
     * @return The number of fingers you should consider "on screen"
     */
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
            if (screen.camera.zoom >= 1.0f) {
                float maxY = screen.topTileWorldCoords.y, minY = screen.bottomTileWorldCoords.y, maxX = screen.rightTileWorldCoords.x,
                        minX = screen.leftTileWorldCoords.x;
                screen.camera.position.x = minX + 0.5f * screen.camera.viewportWidth;
                screen.camera.position.y = minY + 0.5f * screen.camera.viewportHeight;
                return true;
            }
            if (panningLastOrigin != null) {
                Vector2 delta = panningLastOrigin.add(-screenX, -screenY).scl(-1).scl(screen.camera.zoom);
                panCamera(delta);
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

    /**
     * Move the screen camera with a given vector.
     * @param move Move operates by camera.
     */
    private void panCamera(Vector2 move) {
        if (screen.camera.zoom >= 1.0f) return;
        float maxY = screen.topTileWorldCoords.y, minY = screen.bottomTileWorldCoords.y, maxX = screen.rightTileWorldCoords.x,
                minX = screen.leftTileWorldCoords.x;
        screen.camera.translate(move);
        screen.camera.position.x = MathUtils.clamp(screen.camera.position.x, minX + 0.5f * screen.camera.viewportWidth * screen.camera.zoom, maxX - 0.5f * screen.camera.viewportWidth * screen.camera.zoom);
        screen.camera.position.y = MathUtils.clamp(screen.camera.position.y, minY + 0.5f * screen.camera.viewportHeight * screen.camera.zoom, maxY - 0.5f * screen.camera.viewportHeight * screen.camera.zoom);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        System.out.println(amount);
        return false;
    }

    /**
     * Display the selection helper cross on a position
     *
     * @param position The position to display it.
     */
    private void showCrossOn(final Vector2 position) {
        hideCross();
        if (screen.goban.getBoard() != null && screen.goban.getBoard().isGameEnded()) return;
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
            if (initialDistance < screen.viewport.getScreenWidth() * 0.3 && distance < screen.viewport.getScreenWidth() * 0.3)
                return false;
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

    /**
     * Task able to pan camera when a key is down.
     * This class should be run in a thread between key down and key up events.
     */
    private class KeyPanning extends Timer.Task {

        private int keyCode;
        private long time;

        public KeyPanning(int keycode) {
            keyCode = keycode;
            time = System.currentTimeMillis();
        }

        @Override
        public void run() {
            long deltaTime = System.currentTimeMillis() - time;
            int dx = (int) (50 * (deltaTime / 100) * screen.getCamera().zoom); // FIXME : Adjust values
            if (dx == 0) return; // dx is null, wait to be more than 1
            time = System.currentTimeMillis();
            Vector2 move = new Vector2(0, 0);
            switch (keyCode) {
                case Input.Keys.DOWN:
                    move.y -= dx;
                    break;
                case Input.Keys.UP:
                    move.y += dx;
                    break;
                case Input.Keys.LEFT:
                    move.x -= dx;
                    break;
                case Input.Keys.RIGHT:
                    move.x += dx;
                    break;
            }
            panCamera(move);
        }
    }

}
