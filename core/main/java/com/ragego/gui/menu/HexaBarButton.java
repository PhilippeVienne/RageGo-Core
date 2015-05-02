package com.ragego.gui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Button for the {@link HexaBar}.
 */
public class HexaBarButton extends Button {

    /**
     * Menu where this button is displayed.
     */
    private final HexaBar hexabar;

    /**
     * Position of this button in the hexabar.
     */
    private int position;

    private Texture upTexture;
    private Texture downTexture;

    /**
     * @param hexabar The hexabar where the button will be
     * @param upTexToLoad The up texture to load
     * @param downTexToLoad The down texture to load
     * @param position The wanted position in the hex coordinates system
     */
    public HexaBarButton(HexaBar hexabar, String upTexToLoad, String downTexToLoad, int position) {
        this.hexabar = hexabar;
        loadTex(upTexToLoad, downTexToLoad);
        setSize(getPrefWidth(), getPrefHeight());
        setPosition(position);
        hexabar.addButton(this);
    }

    /**
     * Load textures and update variables.
     *
     * @param upTexToLoad URI to give to {@link com.badlogic.gdx.Gdx#files}
     * @param downTexToLoad URI to give to {@link com.badlogic.gdx.Gdx#files}
     */
    private void loadTex(String upTexToLoad, String downTexToLoad) {
        upTexture = new Texture(Gdx.files.classpath(upTexToLoad));
        downTexture = new Texture(Gdx.files.classpath(downTexToLoad));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(upTexture));
        style.down = new TextureRegionDrawable(new TextureRegion(downTexture));
        setStyle(style);
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
        final Vector2 coordinates = hexabar.getCoordinateFor(position);
        coordinates.add(-1.0f * getWidth() * 0.5f, -1.0f * getHeight() * 0.5f);
        setPosition(coordinates.x, coordinates.y);
    }

    /**
     * Dispose texture loaded by this button.
     */
    public void dispose() {
        upTexture.dispose();
        downTexture.dispose();
    }
}
