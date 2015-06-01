package com.ragego.gui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.ragego.engine.GameBoard;
import com.ragego.engine.Player;
import com.ragego.engine.TurnListener;
import com.ragego.gui.GraphicTurnListener;
import com.ragego.gui.RageGoGame;
import com.ragego.gui.elements.HexaFrameBottomButton;
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
     * Timer to timeout the game.
     */
    private PlayerTimer timer;
    /**
     * Turn label
     */
    private Label turnLabel = new Label("It's your turn !", RageGoGame.getUiSkin());
    
    {
        turnLabel.setColor(Color.BLACK);
    }

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
    protected void setupHud() {
        super.setupHud();

        /* Disables the back and forward buttons and changes them into inactive ones.
        *  This will be kept so until a proper strategy is implemented on the server side in order to allow them.
         */
        this.hexaFrameBottom.getButtons().get(1).remove();
        new HexaFrameBottomButton(hexaFrameBottom, 1, "inactive");
        this.hexaFrameBottom.getButtons().get(2).remove();
        new HexaFrameBottomButton(hexaFrameBottom, 2, "inactive");

        float prefHeight = turnLabel.getPrefHeight();
        float prefWidth = turnLabel.getPrefWidth();
        turnLabel.setPosition(hudViewport.getScreenWidth() / 2 - prefWidth / 2, hudViewport.getScreenHeight() / 2 - prefHeight / 2);
        hudStage.addActor(turnLabel);
        turnLabel.setVisible(false);
    }

    @Override
    protected String getMapToLoad() {
        return "goban_19_summer";
    }

    /**
     * Start a new turn timer for a given player.
     * @param player The player which is playing.
     */
    private void startTimer(Player player) {
        if (timer != null)
            timer.stopTimer();
        timer = new PlayerTimer(player);
        timer.start();
        if(player == RageGoServer.getLocalPlayer()){
            turnLabel.setVisible(true);
            new Thread("YourTurnLabelThread"){
                public void run(){
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    turnLabel.setVisible(false);
                }
            }.start();
        }
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
         *
         * @param listener Listener which is called for function mapping
         */
        public OnlineTimerOut(TurnListener listener) {
            this.listener = listener;
        }

        @Override
        public void newTurn(GameBoard board, Player player) {
            listener.newTurn(board, player);
        }

        @Override
        public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {
            listener.endOfTurn(board, player, nextPlayer);
        }

        @Override
        public void startOfTurn(GameBoard board, Player player, Player previousPlayer) {
            startTimer(player);
            listener.startOfTurn(board, player, previousPlayer);
        }
    }

    /**
     * Timer used to exit if the player wait more than 90 seconds to play.
     */
    private class PlayerTimer extends Thread{

        private final Player player;
        /**
         * Time left to play in seconds.
         */
        private int time = 90;
        /**
         * Define if this thread should continue to run.
         */
        private boolean shouldRun = true;

        /**
         * Create a timer for the given player
         * @param player The player acting on this timer
         */
        public PlayerTimer(Player player) {
            this.player = player;
            if(player != RageGoServer.getLocalPlayer()){
                time = 120;
            } else {
                time = 90;
            }
        }

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
                    if(player == RageGoServer.getLocalPlayer())
                        displayDialog(new RageGoDialog("Timeout", "The 90 seconds allowed for a turn are gone. The game is canceled", RageGoDialog.MESSAGE, new Runnable() {
                            @Override
                            public void run() {
                                RageGoGame.goHome();
                            }
                        }));
                    else
                        displayDialog(new RageGoDialog("Timeout", "The other player has gone !"));
                }
                try {
                    Thread.sleep(1000);
                    time--;
                    if(player == RageGoServer.getLocalPlayer())
                        hexaFrameTop.updateTime(time);
                    else
                        hexaFrameTop.clearTime();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
