package com.ragego.gui.elements;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.ragego.gui.screens.MenuScreen;

import java.util.HashMap;

/**
 *  Defines the hexagonal menu in the {@link MenuScreen}
 *  The position of a button is described by an keyword.
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

    /**
     * Adds the given button to the menu after getting its position
     *
     * @param button Button that is to be added to the hexa frame
     */
    public void addButton(HexagonalMenuButton button) {
        buttons.put(button.getPosition(), button);
        this.addActor(button);
    }

    /**
     * Gets the coordinates for a given position
     * Puts the bottom-left corner of the button at the computed coordinates. The button is centered in its own class.
     *
     * @param position The position of the button
     * @return The world coordinates of the corresponding position
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

    /**
     * Gets the menuSkin associated with the menu.
     *
     * @return The menuSkin
     */
    public Skin getMenuSkin() {
        return menuSkin;
    }

    /**
     * Declares the seven acceptable positions in the hexagon menu.
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
