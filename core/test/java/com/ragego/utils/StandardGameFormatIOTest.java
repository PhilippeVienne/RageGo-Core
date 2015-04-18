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
    public void readEmptyGame() {
        formatIO = new StandardGameFormatIO(writeTempFile("empty", "sgf", "(;FF[4]GM[1]SZ[19])"), game);
        try {
            formatIO.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        try {
            loadAGameAndCompute("test-game.sgf");
        } catch (IOException e) {
            throw new RuntimeException("Error while reading", e);
        }
        checkWantedBoard("2305DFE94D9AFD449769616F11D2D9BD", game);
    }

    @Test
    public void read9SizedSimpleBoard() {
        try {
            loadAGameAndCompute("ban9-test-multipleStoneDelete.sgf");
        } catch (IOException e) {
            throw new RuntimeException("Error while reading", e);
        }
        checkWantedBoard("7D5120D1E204AC1DCD74BEBCBFA8B828", game);
    }

    @Test
    public void read9SizedBoard() {
        try {
            loadAGameAndCompute("ban9-test-full.sgf");
        } catch (IOException e) {
            throw new RuntimeException("Error while reading", e);
        }
        checkWantedBoard("FD23B551AB08278F3C32151C6921C205", game);
    }

    @Test
    public void read19SizedBoardTestShapes() {
        try {
            loadAGameAndCompute("ban19-test-shapes.sgf");
        } catch (IOException e) {
            throw new RuntimeException("Error while reading", e);
        }
        checkWantedBoard("62E4A81454883FBE9C981B15BEA7CA36", game);
    }

    @Test
    public void read9SizedBoardDeleteStoneOnBorder() {
        try {
            loadAGameAndCompute("ban9-test-deleteStoneBorder.sgf");
        } catch (IOException e) {
            throw new RuntimeException("Error while reading", e);
        }
        checkWantedBoard("6E38B43E0FA2AD943211D5A6F1E951FE", game);
    }

    /**
     * Load a game and complete compute it.
     * @param resource The game to load.
     * @throws IOException Test file probably not found.
     */
    private void loadAGameAndCompute(String resource) throws IOException{
        formatIO = new StandardGameFormatIO(FileUtils.getResourceAsTMPFile(StandardGameFormatIO.class,resource), game);
        game = formatIO.read();
    }

    private void checkWantedBoard(String gameHash, GameBoard board) {
        if (!(gameHash.equals(board.getBoardHash()))) {
            System.err.println("Should find '" + gameHash + "' but found '" + board.getBoardHash() + "'");
            throw new RuntimeException("The board is not in the wanted state");
        }
    }

}
