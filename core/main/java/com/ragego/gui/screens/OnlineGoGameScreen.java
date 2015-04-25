package com.ragego.gui.screens;

import com.ragego.engine.GameBoard;
import com.ragego.engine.Player;
import com.ragego.gui.GraphicTurnListener;
import com.ragego.gui.objects.Goban;
import com.ragego.network.OnlineGame;
import com.ragego.network.OnlinePlayer;
import com.ragego.network.RageGoServer;

/**
 * Game screen to display an Online Game
 */
public class OnlineGoGameScreen extends GoGameScreen {

    /**
     * Game displayed by this screen.
     */
    private OnlineGame onlineGame;

    public OnlineGoGameScreen(OnlineGame game) {
        super();
        onlineGame = game;
    }

    @Override
    protected void setupGoban(Goban goban) {
        final OnlinePlayer local = RageGoServer.getLocalPlayer();
        OnlinePlayer remote = onlineGame.getWhites() == local ? onlineGame.getBlacks() : onlineGame.getWhites();
        local.setListener(new GraphicTurnListener(this, goban) {
            @Override
            public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {
                super.endOfTurn(board, player, nextPlayer);
                if (player == local) {
                    RageGoServer.createNode(board.getLastNode(), onlineGame, (OnlinePlayer) player);
                }
            }
        });
        local.setCurrentGame(onlineGame);
        remote.setCurrentGame(onlineGame);
        goban.setGameBoard(new GameBoard(onlineGame.getBlacks(), onlineGame.getWhites(), goban.getSize()));
        goban.startGame();
    }

    @Override
    protected String getMapToLoad() {
        return "Goban_world_test";
    }

    public OnlineGame getOnlineGame() {
        return onlineGame;
    }

    public void setOnlineGame(OnlineGame onlineGame) {
        this.onlineGame = onlineGame;
    }
}
