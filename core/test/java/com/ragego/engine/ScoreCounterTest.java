package com.ragego.engine;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Test that score are correctly computed.
 */
public class ScoreCounterTest {

    private GameBoard board;
    private TestPlayer player1;
    private TestPlayer player2;
    private ScoreCounter scoreCounter;

    @Before
    public void init(){
        player1 = new TestPlayer();
        player2 = new TestPlayer();
        board = new GameBoard(player1,player2);
        scoreCounter = board.getScoreCounter();
    }

    @Test
    public void boardShouldReturnAScoreCounter(){
        assertThat(scoreCounter,is(notNullValue()));
    }

    @Test
    public void givePointToOpponentWhenStoneIsCaptivated(){
        player1.registerNodeToPlay(board,"aa","bb");
        board.nextMove();
        player2.registerNodeToPlay(board,"ab");
        board.nextMove();
        board.nextMove();
        assertThat(scoreCounter.getPoints(player1),is(0));
        assertThat(scoreCounter.getPoints(player2),is(0));
        player2.registerNodeToPlay(board,"ba");
        board.nextMove();
        assertThat(scoreCounter.getPoints(player1),is(0));
        assertThat(scoreCounter.getPoints(player2),is(1));
    }

}
