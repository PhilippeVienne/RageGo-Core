package com.ragego.network;

import com.ragego.engine.GameBoard;
import com.ragego.engine.Player;
import com.ragego.engine.TurnListener;

/**
 * Listener for remote player.
 * Let create a RemotePlayer by communicating with a server to play.
 *
 */
public abstract class RemotePlayerListener implements TurnListener {

    @Override
    public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {
        // Send data to server
    }

    @Override
    public void startOfTurn(GameBoard board, Player player, Player previousPlayer) {
        // Check data on server
    }
}
