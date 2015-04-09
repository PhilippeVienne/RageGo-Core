package com.ragego.engine;

/**
 * Turn listener that ignore start and end of turns.
 *
 * @see TurnListener for more details
 */
public abstract class AbstractTurnListener implements TurnListener {

    @Override
    public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {

    }

    @Override
    public void startOfTurn(GameBoard board, Player player, Player previousPlayer) {

    }
}
