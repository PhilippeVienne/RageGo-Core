package com.ragego.gui.screens;

import com.ragego.engine.GameBoard;
import com.ragego.gui.GraphicTurnListener;
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
    public void show() {
        super.show();
        OnlinePlayer local = RageGoServer.getLocalPlayer();
        OnlinePlayer remote = onlineGame.getWhites() == local ? onlineGame.getBlacks() : onlineGame.getWhites();
        local.setListener(new GraphicTurnListener(this, goban));
        goban.setGameBoard(new GameBoard(local, remote, goban.getSize()));
        goban.startGame();
    }

    public OnlineGame getOnlineGame() {
        return onlineGame;
    }

    public void setOnlineGame(OnlineGame onlineGame) {
        this.onlineGame = onlineGame;
    }
}
