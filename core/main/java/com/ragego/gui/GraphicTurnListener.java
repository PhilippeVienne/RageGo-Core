package com.ragego.gui;

import com.ragego.engine.*;
import com.ragego.gui.objects.Goban;
import com.ragego.gui.screens.GoGameScreen;

/**
 * Turn listener for a graphical environment.
 */
public class GraphicTurnListener implements TurnListener {

    private final GoGameScreen screen;
    private Goban goban;

    /**
     * Create a turn listener with a screen and a goban
     *
     * @param screen The {@link GoGameScreen} displayed on screen.
     */
    public GraphicTurnListener(GoGameScreen screen) {
        this(screen, null);
    }

    /**
     * Create a turn listener with a screen and a goban
     *
     * @param screen The {@link GoGameScreen} displayed on screen.
     * @param goban  The goban where this listener act.
     */
    public GraphicTurnListener(GoGameScreen screen, Goban goban) {
        this.screen = screen;
        this.goban = goban;
    }

    @Override
    public void newTurn(GameBoard board, Player player) {
        Intersection intersection = null;

        // INSERT CODE
        // Code to wait a click from player and recive Intersection

        // Apply turn on game
        if (intersection != null) {
            GameNode node = new GameNode(goban.getBoard(), null, GameNode.Action.PUT_STONE, intersection, player);
            goban.getBoard().play(node);
        }
    }

    @Override
    public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {
    }

    @Override
    public void startOfTurn(GameBoard board, Player player, Player previousPlayer) {

    }
}
