package com.ragego.engine;

import com.ragego.utils.DebugUtils;
import com.ragego.utils.FileUtils;
import com.ragego.utils.StandardGameFormatIO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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

    @After
    public void reset() {
        GameBoard.DEBUG_MODE = false;
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
        System.out.println(game.getScoreCounter().formatResult());
    }

    @Test
    public void testScoringAnotherDifficult() throws Exception {
        GameBoard game = new StandardGameFormatIO(FileUtils.getResourceAsTMPFile(StandardGameFormatIO.class, "2015-03-21-3.sgf"), board).read();
        game.getScoreCounter().compute();
        game.getScoreCounter().getScore(new Komi(0.5), ScoreCounter.ScoringMethod.TERRITORY);
        System.out.println(game.getScoreCounter().formatResult());
    }

    @Test
    public void testScoringDifficult() throws Exception {
        GameBoard game = new StandardGameFormatIO(FileUtils.getResourceAsTMPFile(StandardGameFormatIO.class, "2015-03-15-16.sgf"), board).read();
        game.getScoreCounter().compute();
        game.getScoreCounter().getScore(new Komi(0.5), ScoreCounter.ScoringMethod.TERRITORY);
        System.out.println(game.getScoreCounter().formatResult());
    }

    @Test
    public void testScoring() throws IOException {
        GameBoard game = new StandardGameFormatIO(FileUtils.getResourceAsTMPFile(StandardGameFormatIO.class, "2015-03-09-35.sgf"), board).read();
        game.getScoreCounter().compute();
        game.getScoreCounter().getScore(new Komi(6.5), ScoreCounter.ScoringMethod.TERRITORY);
        System.out.println(game.getScoreCounter().formatResult());
    }

}
