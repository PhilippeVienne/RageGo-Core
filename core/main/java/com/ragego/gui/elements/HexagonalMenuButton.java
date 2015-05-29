package com.ragego.gui.elements;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Button for the {@link HexagonalMenu}.
 */
public class HexagonalMenuButton extends Button {
    private final HexagonalMenu menu;
    private HexagonalMenu.Position position;
    private Skin menuSkin;

    public HexagonalMenuButton(HexagonalMenu menu, HexagonalMenu.Position position, String buttonStyleName) {
        this.menu = menu;
        this.menuSkin = menu.getMenuSkin();
        this.setStyle(menuSkin.get(buttonStyleName, ButtonStyle.class));
        setSize(getPrefWidth(), getPrefHeight());
        setPosition(position);
        menu.addButton(this);
    }

    /**
     * Gets the position for this button.
     *
     * @return The position
     */
    public HexagonalMenu.Position getPosition() {
        return position;
    }

    /**
     * Updates the position of a button for a given position
     *
     * @param position The given position for this button
     */
    public void setPosition(HexagonalMenu.Position position) {
        this.position = position;
        final Vector2 coordinates = menu.getCoordinateFor(position);
        coordinates.add(-1.0f * getWidth() * 0.5f, -1.0f * getHeight() * 0.5f);
        setPosition(coordinates.x, coordinates.y);
    }
}
