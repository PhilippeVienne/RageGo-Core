package com.ragego.utils;

import com.ragego.engine.GameBoard;

/**
 * Created by Philippe Vienne on 14/04/2015.
 */
public class DebugUtils {

    public static void printBoard(GameBoard board) {
        final int[][] data = board.getRepresentation();
        StringBuilder builder = new StringBuilder("    ");
        for (int i = 1; i <= data.length; i++) {
            builder.append(' ').append((char) (64 + i)).append(' ');
        }
        builder.append('\n');
        for (int i = 0; i < data.length; i++) {
            int[] line = data[i];
            if (i < 9) {
                builder.append(' ');
            } else {
                builder.append(" ");
            }
            builder.append(i + 1);
            if (i < 9) {
                builder.append(' ');
            }
            builder.append(' ');
            for (int i1 : line) {
                builder.append(' ');
                builder.append(i1);
                builder.append(' ');
            }
            builder.append('\n');
        }
        builder.append("    ");
        for (int i = 1; i <= data.length; i++) {
            builder.append(' ').append((char) (64 + i)).append(' ');
        }
        System.out.println("Le jeu :");
        System.out.println(builder.toString());
    }

}
