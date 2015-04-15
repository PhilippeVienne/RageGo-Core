package com.ragego.utils;

import com.ragego.RageGoTest;
import com.ragego.engine.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

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
        final GameNode lastNode = game.getLastNode();
        assertEquals(lastNode.getAction(), GameNode.Action.START_GAME);
    }

    @Test
    public void readErrorGame() {
        String[] errorSgf = new String[]{
                "",
                "(GM[2])",
                "(C[",
                "(GM[2];AB[aa])",
                "(GM[2];B[zz])",
                "(GM[2];B[s421s])",
        };
        for (String sgf_code : errorSgf) {
            formatIO = new StandardGameFormatIO(writeTempFile("error", "sgf", sgf_code), game);
            try {
                formatIO.read();
            } catch (IOException e) {
                assert e.getMessage().contains("[SGF Error]");
            }
        }
    }

    @Test
    public void readSimpleGame() throws IOException {
        String data = " (;FF[4]GM[1]SZ[19]\n" +
                "\n" +
                " GN[Copyright goproblems.com]\n" +
                " PB[Black]\n" +
                " HA[0]\n" +
                " PW[White]\n" +
                " KM[5.5]\n" +
                " DT[1999-07-21]\n" +
                " TM[1800]\n" +
                " RU[Japanese]\n" +
                "\n" +
                " ;AW[bb][cb][cc][cd][de][df][cg][ch][dh][ai][bi][ci]\n" +
                " AB[ba][ab][ac][bc][bd][be][cf][bg][bh]\n" +
                " C[Black to play and live.]\n" +
                " (;B[af];W[ah]\n" +
                " (;B[ce];W[ag]C[only one eye this way])\n" +
                " (;B[ag];W[ce]))\n" +
                " (;B[ah];W[af]\n" +
                " (;B[ae];W[bf];B[ag];W[bf]\n" +
                " (;B[af];W[ce]C[oops! you can't take this stone])\n" +
                " (;B[ce];W[af];B[bg]C[RIGHT black plays under the stones and lives]))\n" +
                " (;B[bf];W[ae]))\n" +
                " (;B[ae];W[ag]))\n";
        formatIO = new StandardGameFormatIO(writeTempFile("empty", "sgf", data), game);
        formatIO.read();
    }

    @Test
    public void readBigGame() {
        StringBuilder buffer = new StringBuilder();
        InputStream inputStream = StandardGameFormatIO.class.getResourceAsStream("test-game.sgf");
        try {
            while (inputStream.available() != 0)
                buffer.append((char) inputStream.read());
        } catch (IOException e) {
            throw new RuntimeException("Can not load test file", e);
        }
        formatIO = new StandardGameFormatIO(writeTempFile("game", "sgf", buffer.toString()), game);
        try {
            game = formatIO.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        checkWantedBoard("D9C86E953531C0D5E896D075AC0B5C31", game);
    }

    private void checkWantedBoard(String gameHash, GameBoard board) {
        if (!(gameHash.equals(board.getBoardHash()))) {
            System.err.println("Should find '" + gameHash + "' but found '" + board.getBoardHash() + "'");
            throw new RuntimeException("The board is not in the wanted state");
        }
    }

}
