package com.ragego.engine;

/**
 * Counter for player scores in the game.
 * This function manage score between players during a game and help to determine if a party is ended.
 * @author Philippe Vienne
 * @since 1.0
 */
public class ScoreCounter implements GameListener {

    /**
     * Board where the score is being counted.
     */
    private final GameBoard board;

    /**
     * First player
     */
    private final Player firstPlayer;

    /**
     * Second player
     */
    private final Player secondPlayer;

    /**
     * Contain score for each players.
     */
    private final int[] score = new int[2];

    public ScoreCounter(GameBoard board){
        this.board = board;
        this.firstPlayer = board.getFirstPlayer();
        this.secondPlayer = board.getSecondPlayer();
        board.addGameListener(this);
    }

    /**
     * Add points to a player.
     * @param player The player which is wining points
     * @param points Amount of points to add (this could be negative)
     */
    private void addPoint(Player player, int points){
        if(player == firstPlayer){
            score[0] += points;
        } else if(player == secondPlayer){
            score[1] += points;
        } else {
            throw new IllegalArgumentException("Points given to an unknown player");
        }
    }

    /**
     * Get points for a player.
     * @param player Point owned by this player
     * @return The amount of points (0 if the player is not on this board).
     */
    public int getPoints(Player player){
        if(player == firstPlayer){
            return score[0];
        } else if(player == secondPlayer){
            return score[1];
        } else {
            return 0;
        }
    }

    public int[] computeGameScore() {
        final int[] score = new int[2];
        int lastPlayer = 0;
        int bufferScore;
        final int[][] board = this.board.getRepresentation();
        for (int[] line : board) {
            lastPlayer = 0;
            for (int row : line) {
                switch (row) {
                    case 1:
                        if (lastPlayer != 1) {
                            if (lastPlayer == 0) {

                            }
                        }
                        break;
                    case 2:

                        break;
                    default:

                        break;
                }
            }
        }
        return score;
    }

    @Override
    public void newStoneAdded(Stone stone) {
        // No consequences on score
    }

    @Override
    public void stoneRemoved(Stone stone) {
        addPoint(board.getOpponent(stone.getPlayer()),1);
    }

    @Override
    public void playNode(GameNode node) {
        // No consequences on score
    }

    @Override
    public void newTurn(GameBoard board, Player player) {
        // No consequences on score
    }

    @Override
    public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {
        // No consequences on score
    }

    @Override
    public void startOfTurn(GameBoard board, Player player, Player previousPlayer) {
        // No consequences on score
    }
}
