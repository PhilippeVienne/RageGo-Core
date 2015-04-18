package com.ragego.engine;

import java.util.ArrayList;

/**
 * A player should give some information about him.
 */
public abstract class Player {

    /**
     * Store stones attached to this player.
     */
    protected ArrayList<Stone> stones;

    /**
     * Store other player stones capturated by this user.
     */
    protected ArrayList<Stone> capturatedStones;

    /**
     * Store stoneGroups attached to this player
     */
    protected ArrayList<StoneGroup> stoneGroups;

    /**
     * Listener for turns.
     */
    protected TurnListener listener;

    /**
     * Describe how to name this player
     *
     * @return Human readable and pretty string to name this player
     */
    public abstract String getDisplayName();

    /**
     * @return The turn listener for this player
     */
    public TurnListener getListener() {
        return listener;
    }

    /**
     * Update the listener for the given player.
     */
    public void setListener(TurnListener listener) {
        this.listener = listener;
    }
}
