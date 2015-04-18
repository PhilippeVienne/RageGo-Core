package com.ragego.gui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;

/**
 * Manages positions and display for an hexagonal menu.
 */
public class HexagonalMenu {

    private static final String MENU_BACKGROUND_TEXTURE = "android/assets/ui/main_menu/menu_back.png";
    private final Texture backgroundTexture;
    private final Image menuBackground;
    private HashMap<Position, HexagonalButton> buttons = new HashMap<Position, HexagonalButton>(7);
    private Stage stage;

    public HexagonalMenu(Viewport viewport, Stage stage) {
        this.stage = stage;
        Vector2 screenCenter = new Vector2(viewport.getScreenWidth() * 0.5f, viewport.getScreenHeight() * 0.5f);
        backgroundTexture = new Texture(Gdx.files.internal(MENU_BACKGROUND_TEXTURE));
        menuBackground = new Image(backgroundTexture);
        Vector2 menuBackCenter = new Vector2(menuBackground.getWidth() * 0.5f, menuBackground.getHeight() * 0.5f);
        menuBackground.setPosition(screenCenter.x - menuBackCenter.x, screenCenter.y - menuBackCenter.y);
        stage.addActor(menuBackground);
    }

    public void addButton(HexagonalButton button) {
        buttons.put(button.getPosition(), button);
        stage.addActor(button);
    }

    /**
     * Gets the coordinates for a given position
     * Gives the centered position for a position. You should center your object after.
     *
     * @param position The position you want
     * @return The coordinate corresponding to the position
     */
    public Vector2 getCoordinateFor(Position position) {
        Vector2 coordinates = new Vector2(menuBackground.getX(), menuBackground.getY());
        switch (position) {
            case TOP:
                coordinates.add(menuBackground.getWidth() * 0.5f, menuBackground.getHeight() * 0.825f);
                break;
            case RIGHT_TOP:
                coordinates.add(menuBackground.getWidth() * 0.825f, menuBackground.getHeight() * 0.665f);
                break;
            case LEFT_TOP:
                coordinates.add(menuBackground.getWidth() * 0.175f, menuBackground.getHeight() * 0.665f);
                break;
            case RIGHT_BOTTOM:
                coordinates.add(menuBackground.getWidth() * 0.825f, menuBackground.getHeight() * 0.34f);
                break;
            case LEFT_BOTTOM:
                coordinates.add(menuBackground.getWidth() * 0.175f, menuBackground.getHeight() * 0.34f);
                break;
            case BOTTOM:
                coordinates.add(menuBackground.getWidth() * 0.5f, menuBackground.getHeight() * 0.175f);
                break;
            case CENTER:
                coordinates.add(menuBackground.getWidth() * 0.5f, menuBackground.getHeight() * 0.5f);
                break;
        }
        return coordinates;
    }

    public void dispose() {
        this.backgroundTexture.dispose();
        for (HexagonalButton hexagonalButton : buttons.values()) {
            hexagonalButton.dispose();
        }
    }

    /**
     * Declares the corner positions of an hexagon.
     */
    public enum Position {
        TOP,
        RIGHT_TOP,
        LEFT_TOP,
        RIGHT_BOTTOM,
        LEFT_BOTTOM,
        BOTTOM,
        CENTER
    }

}
