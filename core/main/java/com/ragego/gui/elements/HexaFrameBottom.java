package com.ragego.gui.elements;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.ragego.gui.screens.GoGameScreen;

import java.util.HashMap;

/**
 *  Defines the button bar at the bottom of the HUD in the {@link GoGameScreen}
 *  The position of a button is described by an integer that increases from left to right, starting from 1.
 */
public class HexaFrameBottom extends WidgetGroup {
    private static final int BUTTONS_NB = 11;
    private final static String HEXA_FRAME_NAME = "frame_bottom";

    private Skin hudSkin;
    private Image hexaFrameBottomImage;
    private HashMap<Integer, HexaFrameBottomButton> buttons = new HashMap<Integer, HexaFrameBottomButton>(BUTTONS_NB);

    public HexaFrameBottom(Skin hudSkin) {
        super();
        this.hudSkin = hudSkin;
        hexaFrameBottomImage = new Image(hudSkin.getRegion(HEXA_FRAME_NAME));
        setWidth(hexaFrameBottomImage.getWidth());
        setHeight(hexaFrameBottomImage.getHeight());
        addActor(hexaFrameBottomImage);
    }

    /**
     * Adds the given button to the hexa frame after getting its position
     *
     * @param button Button that is to be added to the hexa frame
     */
    public void addButton(HexaFrameBottomButton button) {
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
    public Vector2 getCoordinateFor(int position) {
        Vector2 coordinates = new Vector2(0, 0);
        coordinates.x += hexaFrameBottomImage.getWidth() * (position + 2) / (BUTTONS_NB + 5);
        if (position % 2 == 0)
            coordinates.y += hexaFrameBottomImage.getHeight() * 5 / 7;
        else
            coordinates.y += hexaFrameBottomImage.getHeight() * 2 / 7;
        return coordinates;
    }

    /**
     * Gets the hudSkin associated with the hexa frame.
     *
     * @return The hudSkin
     */
    public Skin getHudSkin() {
        return hudSkin;
    }
}
