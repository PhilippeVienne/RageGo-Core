package com.ragego.network;

import com.ragego.engine.GameBoard;
import com.ragego.engine.Player;
import com.ragego.engine.TurnListener;

public class OnlinePlayerListener implements TurnListener {

    private OnlineGame currentGame;

    @Override
    public void newTurn(GameBoard board, Player player) {
        if (player instanceof OnlinePlayer) {
            currentGame = ((OnlinePlayer) player).getCurrentGame();
            OnlineNode node = currentGame.waitForNewNode((OnlinePlayer) player, board);
            board.play(node.getNode());
        } else {
            throw new IllegalArgumentException("Player should be an online player");
        }
    }

    @Override
    public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {

    }

    @Override
    public void startOfTurn(GameBoard board, Player player, Player previousPlayer) {

    }
}
