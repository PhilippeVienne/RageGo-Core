package com.ragego.gui.elements;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Button for the {@link HexaFrameBottom}.
 */
public class HexaFrameBottomButton extends Button {
    private final HexaFrameBottom hexaFrameBottom;
    private int position;
    private Skin hudSkin;

    public HexaFrameBottomButton(HexaFrameBottom hexaFrameBottom, int position, String buttonStyleName) {
        this.hexaFrameBottom = hexaFrameBottom;
        hudSkin = hexaFrameBottom.getHudSkin();
        setStyle(hudSkin.get(buttonStyleName, ButtonStyle.class));
        setSize(getPrefWidth(), getPrefHeight());
        setPosition(position);
        hexaFrameBottom.addButton(this);
    }

    /**
     * Gets the position for this button.
     *
     * @return The position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Updates the position of a button for a given position
     *
     * @param position The given position for this button
     */
    public void setPosition(int position) {
        this.position = position;
        final Vector2 coordinates = hexaFrameBottom.getCoordinateFor(position);
        coordinates.add(-1.0f * getWidth() * 0.5f, -1.0f * getHeight() * 0.5f);
        setPosition(coordinates.x, coordinates.y);
    }
}
