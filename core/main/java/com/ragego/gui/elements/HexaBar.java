package com.ragego.gui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages positions and display for an hexagonal menu.
 */
public class HexaBar {

    private static final String BAR_BACKGROUND_TEXTURE = "com/ragego/gui/hexabar/frame_white.png";
    private static final int BUTTONS_NB = 11;
    public final Button hexaBar;
    private final Texture backgroundTexture;
    private HashMap<Integer, HexaBarButton> buttons = new HashMap<Integer, HexaBarButton>(BUTTONS_NB);
    private Stage stage;
    private Vector2 hexaBarCenter;

    public HexaBar(Viewport viewport, Stage stage) {
        this.stage = stage;
        backgroundTexture = new Texture(Gdx.files.classpath(BAR_BACKGROUND_TEXTURE));
        Button.ButtonStyle hexaBarStyle = new Button.ButtonStyle();
        hexaBarStyle.up = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        hexaBar = new Button(hexaBarStyle);
        hexaBarCenter = new Vector2(hexaBar.getWidth() * 0.5f, hexaBar.getHeight() * 0.5f);
        hexaBar.setPosition(viewport.getScreenWidth() * 0.5f - hexaBarCenter.x, 0);
        stage.addActor(hexaBar);
    }

    public void addButton(HexaBarButton button) {
        buttons.put(button.getPosition(), button);
        stage.addActor(button);
    }
    public void update (Viewport viewport) {
        hexaBarCenter.x = hexaBar.getWidth() * 0.5f;
        hexaBarCenter.y = hexaBar.getHeight() * 0.5f;
        hexaBar.setPosition(viewport.getScreenWidth() * 0.5f - hexaBarCenter.x, 0);
        for (Map.Entry<Integer, HexaBarButton> buttonEntry : buttons.entrySet()) {
            buttonEntry.getValue().setPosition(buttonEntry.getKey());
        }
    }

    /**
     * Gets the coordinates for a given position
     * Gives the centered position for a position. You should center your object after.
     *
     * @param position The position you want
     * @return The coordinate corresponding to the position
     */
    public Vector2 getCoordinateFor(int position) {
        Vector2 coordinates = new Vector2(hexaBar.getX(), hexaBar.getY());
        coordinates.x += hexaBar.getWidth() * position / (BUTTONS_NB + 1);
        if (position%2==0)
            coordinates.y += hexaBar.getHeight() * 5 / 7;
        else
            coordinates.y += hexaBar.getHeight() * 2 / 7;
        return coordinates;
    }

    public void dispose() {
        this.backgroundTexture.dispose();
        for (HexaBarButton button : buttons.values()) {
            button.dispose();
        }
    }
}
