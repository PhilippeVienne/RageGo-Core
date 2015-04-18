package com.ragego.engine;

import com.ragego.engine.AbstractTurnListener;
import com.ragego.engine.GameBoard;
import com.ragego.engine.Player;
import com.ragego.engine.TestPlayer;

/**
 * A simple turn listener for {@link TestPlayer} that plays turns.
 */
public class TestPlayerTurnListener extends AbstractTurnListener {

    private final TestPlayer player;

    public TestPlayerTurnListener(TestPlayer player) {
        this.player = player;
    }

    @Override
    public void newTurn(GameBoard board, Player playerFromBoard) {
        if (player.hasNextNode()) {
            board.play(player.getNextNode());
        } else {
            throw new IllegalStateException("Has no more turn to play ...");
        }
    }
}