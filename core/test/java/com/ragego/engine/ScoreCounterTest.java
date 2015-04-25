package com.ragego.engine;

import com.ragego.utils.DebugUtils;
import com.ragego.utils.FileUtils;
import com.ragego.utils.StandardGameFormatIO;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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
    public void givePointToOpponentWhenStoneIsCaptivated() throws Exception {
        GameBoard game = new StandardGameFormatIO(FileUtils.getResourceAsTMPFile(StandardGameFormatIO.class, "2015-03-01-19.sgf"), board).read();
        game.getScoreCounter().compute();
        game.getScoreCounter().getScore(new Komi(0.5), ScoreCounter.ScoringMethod.TERRITORY);
        DebugUtils.printBoard(game);
        System.out.println("hello");
    }

}
