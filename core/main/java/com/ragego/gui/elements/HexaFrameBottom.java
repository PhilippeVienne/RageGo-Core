package com.ragego.gui.elements;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import java.util.HashMap;

/**
 * Manages positions and display for an hexagonal bar.
 */
public class HexaFrameBottom extends WidgetGroup {
    private static final int BUTTONS_NB = 11;
    private final static String HEXA_FRAME_NAME = "frame_bottom";

    private Skin hudSkin;
    private Image hexaFrameBottom;
    private HashMap<Integer, HexaFrameBottomButton> buttons = new HashMap<Integer, HexaFrameBottomButton>(BUTTONS_NB);

    public HexaFrameBottom(Skin hudSkin) {
        super();
        this.hudSkin = hudSkin;
        hexaFrameBottom = new Image(hudSkin.getRegion(HEXA_FRAME_NAME));
        this.setWidth(hexaFrameBottom.getWidth());
        this.setHeight(hexaFrameBottom.getHeight());
        this.addActor(hexaFrameBottom);
    }

    public void addButton(HexaFrameBottomButton button) {
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
    public Vector2 getCoordinateFor(int position) {
        Vector2 coordinates = new Vector2(0, 0);
        coordinates.x += hexaFrameBottom.getWidth() * (position + 2) / (BUTTONS_NB + 5);
        if (position%2==0)
            coordinates.y += hexaFrameBottom.getHeight() * 5 / 7;
        else
            coordinates.y += hexaFrameBottom.getHeight() * 2 / 7;
        return coordinates;
    }

    public Skin getHudSkin() {
        return hudSkin;
    }
}
