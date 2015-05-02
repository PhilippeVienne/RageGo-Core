package com.ragego.gui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.HashMap;

/**
 * Manages positions and display for an hexagonal menu.
 */
public class HexaBar {

    private static final String BAR_BACKGROUND_TEXTURE = "com/ragego/gui/hexabar/frame_white.png";
    private static final int BUTTONS_NB = 11;
    private final Texture backgroundTexture;
    private final Image hexaBarBackground;
    private HashMap<Integer, HexaBarButton> buttons = new HashMap<Integer, HexaBarButton>(BUTTONS_NB);
    private Stage stage;

    public HexaBar(Stage stage) {
        this.stage = stage;
        backgroundTexture = new Texture(Gdx.files.classpath(BAR_BACKGROUND_TEXTURE));
        hexaBarBackground = new Image(backgroundTexture);
        Vector2 menuBackCenter = new Vector2(hexaBarBackground.getWidth() * 0.5f, hexaBarBackground.getHeight() * 0.5f);
        hexaBarBackground.setPosition(stage.getWidth() * 0.5f - menuBackCenter.x, 0);
        stage.addActor(hexaBarBackground);
    }

    public void addButton(HexaBarButton button) {
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
    public Vector2 getCoordinateFor(int position) {
        Vector2 coordinates = new Vector2(hexaBarBackground.getX(), hexaBarBackground.getY());
        coordinates.x += hexaBarBackground.getWidth() * position / (BUTTONS_NB + 1);
        if (position%2==0)
            coordinates.y += hexaBarBackground.getHeight() * 5 / 7;
        else
            coordinates.y += hexaBarBackground.getHeight() * 2 / 7;
        return coordinates;
    }

    public void dispose() {
        this.backgroundTexture.dispose();
        for (HexaBarButton hexagonalButton : buttons.values()) {
            hexagonalButton.dispose();
        }
    }
}
