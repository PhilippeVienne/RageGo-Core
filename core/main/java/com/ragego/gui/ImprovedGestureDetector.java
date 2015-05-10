package com.ragego.gui;

import com.badlogic.gdx.input.GestureDetector;
import com.ragego.utils.GuiUtils;

/**
 * Created by Philippe Vienne on 10/05/2015.
 */
public class ImprovedGestureDetector extends GestureDetector {
    public ImprovedGestureDetector(GestureListener listener) {
        super(listener);
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        if (GuiUtils.getFingersOnScreen() != 2) return false;
        return super.touchDown(x, y, pointer, button);
    }

    @Override
    public boolean touchDragged(float x, float y, int pointer) {
        if (GuiUtils.getFingersOnScreen() != 2) return false;
        return super.touchDragged(x, y, pointer);
    }

    @Override
    public boolean touchUp(float x, float y, int pointer, int button) {
        if (GuiUtils.getFingersOnScreen() != 2) return false;
        return super.touchUp(x, y, pointer, button);
    }
}
