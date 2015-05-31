package com.ragego.gui.screens;

import com.ragego.engine.GameBoard;
import com.ragego.engine.Player;
import com.ragego.engine.TurnListener;
import com.ragego.gui.GraphicTurnListener;
import com.ragego.gui.RageGoGame;
import com.ragego.gui.elements.RageGoDialog;
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

    /**
     * Create a new game screen for an online game.
     * @param game The game which should be played on this screen.
     */
    public OnlineGoGameScreen(OnlineGame game) {
        super();
        onlineGame = game;
    }

    @Override
    protected void setupGoban(Goban goban) {
        final OnlinePlayer local = RageGoServer.getLocalPlayer();
        OnlinePlayer remote = onlineGame.getWhites() == local ? onlineGame.getBlacks() : onlineGame.getWhites();
        local.setListener(new OnlineTimerOut(new GraphicTurnListener(this, goban) {
            @Override
            public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {
                super.endOfTurn(board, player, nextPlayer);
                if (player == local) {
                    RageGoServer.createNode(board.getLastNode(), onlineGame, (OnlinePlayer) player);
                }
            }
        }));
        remote.setListener(new OnlineTimerOut(remote.getListener()));
        local.setCurrentGame(onlineGame);
        remote.setCurrentGame(onlineGame);
        goban.setGameBoard(new GameBoard(onlineGame.getBlacks(), onlineGame.getWhites(), goban.getSize()));
        goban.startGame();
    }

    @Override
    protected String getMapToLoad() {
        return "Goban_world_test";
    }

    /**
     * Firewall class to start and stop timer for the player.
     * Each functions are mapped to the listener given to the constructor.
     */
    private class OnlineTimerOut implements TurnListener {

        /**
         * Listener which is called for function mapping
         */
        private final TurnListener listener;

        /**
         * Create a new timeout
         * @param listener Listener which is called for function mapping
         */
        public OnlineTimerOut(TurnListener listener){
            this.listener = listener;
        }

        @Override
        public void newTurn(GameBoard board, Player player) {
            listener.newTurn(board, player);
        }

        @Override
        public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {
            stopTimer(player);
            listener.endOfTurn(board, player, nextPlayer);
        }

        @Override
        public void startOfTurn(GameBoard board, Player player, Player previousPlayer) {
            startTimer(player);
            listener.startOfTurn(board, player, previousPlayer);
        }
    }

    /**
     * Stop the timer when a player has played.
     * @param player The player who has played.
     */
    private void stopTimer(Player player) {
        timer.stopTimer();
    }

    /**
     * Timer to timeout the game.
     */
    private PlayerTimer timer;

    /**
     * Start a new turn timer for a given player.
     * @param player The player which is playing.
     */
    private void startTimer(Player player) {
        if(timer!=null)
            timer.stopTimer();
        timer = new PlayerTimer();
        timer.start();
    }

    /**
     * Timer used to exit if the player wait more than 90 seconds to play.
     */
    private class PlayerTimer extends Thread{

        /**
         * Time left to play in seconds.
         */
        private int time = 90;
        /**
         * Define if this thread should continue to run.
         */
        private boolean shouldRun = true;

        /**
         * Stop this timer. You call this function when player has played.
         */
        public void stopTimer(){
            shouldRun = false;
        }

        @Override
        public void run() {
            while (shouldRun){
                if(time<=0){
                    shouldRun = false;
                    new RageGoDialog("Timeout", "The 90 seconds allowed for a turn are gone. The game is canceled", RageGoDialog.MESSAGE, new Runnable() {
                        @Override
                        public void run() {
                            RageGoGame.goHome();
                        }
                    }).centerOnViewport(hudViewport).displayOn(hudStage);
                }
                try {
                    Thread.sleep(1000);
                    time--;
                    hexaFrameTop.updateTime(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
