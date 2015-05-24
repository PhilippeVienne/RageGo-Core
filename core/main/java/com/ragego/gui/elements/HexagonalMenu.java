package com.ragego.gui.elements;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import java.util.HashMap;

/**
 * Manages positions and display for an hexagonal menu.
 */
public class HexagonalMenu extends WidgetGroup {
    private static final int BUTTONS_NB = 7;
    private final static String MENU_FRAME_NAME = "menu_frame";
    private Skin menuSkin;
    private Image menuFrame;
    private HashMap<Position, HexagonalMenuButton> buttons = new HashMap<Position, HexagonalMenuButton>(BUTTONS_NB);

    public HexagonalMenu(Skin menuSkin) {
        super();
        this.menuSkin = menuSkin;
        menuFrame = new Image(menuSkin.getRegion(MENU_FRAME_NAME));
        this.setWidth(menuFrame.getWidth());
        this.setHeight(menuFrame.getHeight());
        this.addActor(menuFrame);
    }

    public void addButton(HexagonalMenuButton button) {
        buttons.put(button.getPosition(), button);
        this.addActor(button);
    }

    /**
     * Gets the coordinates for a given position
     * Gives the centered position for a position. You should center your object after.
     *
     * @param position The position you want
     * @return The coordinate corresponding to the position
     */
    public Vector2 getCoordinateFor(Position position) {
        Vector2 coordinates = new Vector2(0, 0);
        switch (position) {
            case TOP:
                coordinates.add(menuFrame.getWidth() * 0.5f, menuFrame.getHeight() * 0.825f);
                break;
            case RIGHT_TOP:
                coordinates.add(menuFrame.getWidth() * 0.825f, menuFrame.getHeight() * 0.665f);
                break;
            case LEFT_TOP:
                coordinates.add(menuFrame.getWidth() * 0.175f, menuFrame.getHeight() * 0.665f);
                break;
            case RIGHT_BOTTOM:
                coordinates.add(menuFrame.getWidth() * 0.825f, menuFrame.getHeight() * 0.34f);
                break;
            case LEFT_BOTTOM:
                coordinates.add(menuFrame.getWidth() * 0.175f, menuFrame.getHeight() * 0.34f);
                break;
            case BOTTOM:
                coordinates.add(menuFrame.getWidth() * 0.5f, menuFrame.getHeight() * 0.175f);
                break;
            case CENTER:
                coordinates.add(menuFrame.getWidth() * 0.5f, menuFrame.getHeight() * 0.5f);
                break;
        }
        return coordinates;
    }

    public Skin getMenuSkin() {
        return menuSkin;
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
