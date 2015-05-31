package com.ragego.gui.elements;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ragego.gui.screens.GoGameScreen;

import java.util.HashMap;

/**
 *  Defines the button bar at the bottom of the HUD in the {@link GoGameScreen}
 *  The position of a button is described by an integer that increases from left to right, starting from 1.
 */
public class HexaFrameBottom extends WidgetGroup {
    private static final int BUTTONS_NB = 11;
    private final static String HEXA_FRAME_NAME = "frame_bottom";

    private boolean isHidden = true;
    private Skin hudSkin;
    private Image hexaFrameImage;
    private WidgetGroup frameVisibleGroup = new WidgetGroup();
    private Button frameHiddenButton;

    private HashMap<Integer, HexaFrameBottomButton> buttons = new HashMap<Integer, HexaFrameBottomButton>(BUTTONS_NB);

    public HexaFrameBottom(Skin hudSkin) {
        super();
        this.hudSkin = hudSkin;
        hexaFrameImage = new Image(hudSkin.getRegion(HEXA_FRAME_NAME));
        setWidth(hexaFrameImage.getWidth());
        setHeight(hexaFrameImage.getHeight());

        frameHiddenButton = new Button(hudSkin, "frame_bottom_hidden");
        frameHiddenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide(false);
            }
        });
        frameHiddenButton.setPosition(0, 0);

        frameVisibleGroup.addActor(hexaFrameImage);
        addActor(frameVisibleGroup);
        addActor(frameHiddenButton);
    }

    /**
     * Adds the given button to the hexa frame after getting its position
     *
     * @param button Button that is to be added to the hexa frame
     */
    public void addButton(HexaFrameBottomButton button) {
        buttons.put(button.getPosition(), button);
        frameVisibleGroup.addActor(button);
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
        coordinates.x += hexaFrameImage.getWidth() * (position + 2) / (BUTTONS_NB + 5);
        if (position % 2 == 0)
            coordinates.y += hexaFrameImage.getHeight() * 5 / 7;
        else
            coordinates.y += hexaFrameImage.getHeight() * 2 / 7;
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

    public void hide(boolean state) {
        isHidden = state;
        if (isHidden) {
            frameVisibleGroup.setVisible(false);
            frameHiddenButton.setVisible(true);
        } else {
            frameVisibleGroup.setVisible(true);
            frameHiddenButton.setVisible(false);
        }
    }

    public Button getHiddenButton() {
        return frameHiddenButton;
    }

    public HashMap<Integer, HexaFrameBottomButton> getButtons() {
        return buttons;
    }
}
