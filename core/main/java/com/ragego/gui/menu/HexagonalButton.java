package com.ragego.gui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Button for the {@link HexagonalMenu}.
 */
public class HexagonalButton extends Button {

    /**
     * Menu where this button is displayed.
     */
    private final HexagonalMenu menu;

    /**
     * Position of this button in the menu.
     */
    private HexagonalMenu.Position position;

    private Texture upTexture;

    /**
     * @param menu           The menu where the button will be
     * @param resourceToLoad The resource to load
     * @param position       The wanted position
     */
    public HexagonalButton(HexagonalMenu menu, String resourceToLoad, HexagonalMenu.Position position) {
        this.menu = menu;
        loadResource(resourceToLoad);
        setSize(getPrefWidth(), getPrefHeight());
        setPosition(position);
        menu.addButton(this);
    }

    /**
     * Load textures and update variables.
     *
     * @param resourceToLoad URI to give to {@link com.badlogic.gdx.Gdx#files}
     */
    private void loadResource(String resourceToLoad) {
        upTexture = new Texture(Gdx.files.internal(resourceToLoad));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(upTexture));
        setStyle(style);
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
     * Update the position of button on a given {@link com.ragego.gui.menu.HexagonalMenu.Position}.
     *
     * @param position The wanted position for this button
     */
    public void setPosition(HexagonalMenu.Position position) {
        this.position = position;
        final Vector2 coordinates = menu.getCoordinateFor(position);
        coordinates.add(-1.0f * getWidth() * 0.5f, -1.0f * getHeight() * 0.5f);
        setPosition(coordinates.x, coordinates.y);
    }

    /**
     * Dispose texture loaded by this button.
     */
    public void dispose() {
        upTexture.dispose();
    }
}
