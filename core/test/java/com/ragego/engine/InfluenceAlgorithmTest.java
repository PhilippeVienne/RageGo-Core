package com.ragego.engine;

import com.ragego.utils.FileUtils;
import com.ragego.utils.StandardGameFormatIO;
import org.junit.Before;
import org.junit.Test;

import java.text.DecimalFormat;

public class InfluenceAlgorithmTest {

    public GameBoard board;

    @Before
    public void setUp() throws Exception {
        AbstractTurnListener listener = new AbstractTurnListener() {
            @Override
            public void newTurn(GameBoard board, Player player) {

            }
        };
        HumanPlayer humanPlayer1 = new HumanPlayer("Player 1", listener);
        HumanPlayer humanPlayer2 = new HumanPlayer("Player 2", listener);
        GameBoard game = new GameBoard(humanPlayer1, humanPlayer2);
        GameBoard.DEBUG_MODE = false;
        this.board = new StandardGameFormatIO(FileUtils.getResourceAsTMPFile(StandardGameFormatIO.class, "2015-03-01-19.sgf"), game).read();
    }

    @Test
    public void testInfluence() {
        InfluenceAlgorithm influenceAlgorithm = new InfluenceAlgorithm(board);
        System.out.println("Model: ");
        for (double[] lines : reverseArray(influenceAlgorithm.initArrayTo1Values(board.getBoardSize()))) {
            for (double value : lines) {
                System.out.print(value);
                System.out.print(' ');
            }
            System.out.println();
        }
        System.out.println("Strength: ");
        for (double[] lines : reverseArray(influenceAlgorithm.getStrength())) {
            for (double value : lines) {
                System.out.print(value);
                System.out.print(' ');
            }
            System.out.println();
        }
        System.out.println("White Strength: ");
        final double[][] whiteByLine = reverseArray(influenceAlgorithm.getWhiteStrength());
        final double[][] blacksByLine = reverseArray(influenceAlgorithm.getBlackStrength());
        final DecimalFormat decimalFormat = new DecimalFormat("0000.0");
        for (int l = 0; l < whiteByLine.length; l++) {
            for (int c = 0; c < whiteByLine[l].length; c++) {
                final double number = blacksByLine[l][c] - whiteByLine[l][c];
                final String format = decimalFormat.format(number);
                if (number > 0)
                    System.out.print(' ');
                System.out.print(format);
                for (int i = format.length() + (number > 0 ? 1 : 0); i < 10; i++) {
                    System.out.print(' ');
                }
            }
            System.out.println();
        }
        for (int l = 0; l < whiteByLine.length; l++) {
            for (int c = 0; c < whiteByLine[l].length; c++) {
                final double number = blacksByLine[l][c] - whiteByLine[l][c];
                System.out.print(number > 0 ? 'B' : 'W');
            }
            System.out.println();
        }
    }

    private double[][] reverseArray(double[][] strength) {
        double[][] reversed = new double[strength.length][strength.length];
        for (int c = 0; c < strength.length; c++) {
            for (int l = 0; l < strength[c].length; l++) {
                reversed[l][c] = strength[c][l];
            }
        }
        return reversed;
    }
}