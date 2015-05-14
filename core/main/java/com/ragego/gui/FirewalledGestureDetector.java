package com.ragego.gui;

import com.badlogic.gdx.input.GestureDetector;
import com.ragego.utils.GuiUtils;

/**
 * Firewall for events on screen with 2 touch points.
 * It declares all touch events with two touch points on screen as computed. It acts like an event propagation firewall.
 *
 * @see GestureDetector to know what this class do.
 */
public class FirewalledGestureDetector extends GestureDetector {
    public FirewalledGestureDetector(GestureListener listener) {
        super(listener);
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return GuiUtils.getFingersOnScreen() == 2 && super.touchDown(x, y, pointer, button);
    }

    @Override
    public boolean touchDragged(float x, float y, int pointer) {
        return GuiUtils.getFingersOnScreen() == 2 && super.touchDragged(x, y, pointer);
    }

    @Override
    public boolean touchUp(float x, float y, int pointer, int button) {
        return GuiUtils.getFingersOnScreen() == 2 && super.touchUp(x, y, pointer, button);
    }
}
