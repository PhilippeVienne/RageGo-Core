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

    /**
     * @param position The wanted position in the hex coordinates system
     */
    public HexaFrameBottomButton(HexaFrameBottom hexaFrameBottom, int position, String buttonStyleName) {
        this.hexaFrameBottom = hexaFrameBottom;
        this.hudSkin = hexaFrameBottom.getHudSkin();
        this.setStyle(hudSkin.get(buttonStyleName, ButtonStyle.class));
        setSize(getPrefWidth(), getPrefHeight());
        setPosition(position);
        hexaFrameBottom.addButton(this);
    }

    /**
     * Get the used position for this button.
     *
     * @return The position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Update the position of button on a given position
     *
     * @param position The wanted position for this button
     */
    public void setPosition(int position) {
        this.position = position;
        final Vector2 coordinates = hexaFrameBottom.getCoordinateFor(position);
        coordinates.add(-1.0f * getWidth() * 0.5f, -1.0f * getHeight() * 0.5f);
        setPosition(coordinates.x, coordinates.y);
    }
}
