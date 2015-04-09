package com.ragego.utils;

import com.ragego.RageGoTest;
import com.ragego.engine.AbstractTurnListener;
import com.ragego.engine.GameBoard;
import com.ragego.engine.HumanPlayer;
import com.ragego.engine.Player;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Test if SGF files are correctly read
 */
public class StandardGameFormatIOTest extends RageGoTest {

    StandardGameFormatIO formatIO;
    HumanPlayer humanPlayer1;
    HumanPlayer humanPlayer2;
    private GameBoard game;

    @Before
    public void initGame() {
        AbstractTurnListener listener = new AbstractTurnListener() {
            @Override
            public void newTurn(GameBoard board, Player player) {

            }
        };
        humanPlayer1 = new HumanPlayer("Player 1", listener);
        humanPlayer2 = new HumanPlayer("Player 2", listener);
        game = new GameBoard(humanPlayer1, humanPlayer2);
    }

    @Test
    public void readEmptyGame() throws IOException {
        formatIO = new StandardGameFormatIO(writeTempFile("empty", "sgf", "(;FF[4]GM[1]SZ[19])"), game);
        formatIO.read();
    }

    @Test
    public void readErrorGame() {

    }

    @Test
    public void readSimpleGame() {

    }

    @Test
    public void readBigGame() {

    }

}
