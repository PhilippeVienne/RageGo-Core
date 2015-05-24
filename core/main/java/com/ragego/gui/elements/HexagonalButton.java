package com.ragego.gui.elements;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Button for the {@link HexagonalMenu}.
 */
public class HexagonalButton extends Button {
    private final HexagonalMenu menu;
    private HexagonalMenu.Position position;
    private Skin menuSkin;

    /**
     * @param menu           The menu where the button will be
     * @param position       The wanted position
     */
    public HexagonalButton(HexagonalMenu menu, HexagonalMenu.Position position, Skin menuSkin, String buttonStyleName) {
        this.menu = menu;
        this.menuSkin = menuSkin;
        this.setStyle(menuSkin.get(buttonStyleName, ButtonStyle.class));
        setSize(getPrefWidth(), getPrefHeight());
        setPosition(position);
        menu.addButton(this);
    }

    /**
     * Get the used position for this button.
     *
     * @return The position
     */
    public HexagonalMenu.Position getPosition() {
        return position;
    }

    /**
     * Update the position of button on a given {@link HexagonalMenu.Position}.
     *
     * @param position The wanted position for this button
     */
    public void setPosition(HexagonalMenu.Position position) {
        this.position = position;
        final Vector2 coordinates = menu.getCoordinateFor(position);
        coordinates.add(-1.0f * getWidth() * 0.5f, -1.0f * getHeight() * 0.5f);
        setPosition(coordinates.x, coordinates.y);
    }
}
