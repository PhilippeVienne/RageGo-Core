package com.ragego.engine;

import java.util.Arrays;

/**
 * A board snap is an edit less situation of the game.
 * A board snap is useful to compare or store two game states.
 */
public class BoardSnap {

    private int[][] data;

    public BoardSnap(GameBoard board){
        if(board == null)
            throw new IllegalArgumentException("Board can not be null");
        data = board.getRepresentation();
    }

    public BoardSnap(int[][] representation) {
        data = Arrays.copyOf(representation,representation.length);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BoardSnap && equals(data, ((BoardSnap) obj).data);
    }

    private boolean equals(int[][] data, int[][] data1) {
        if(data.length != data1.length)
            return false;
        for (int i = 0; i < data.length; i++) {
            if(!Arrays.equals(data[i],data1[i]))
                return false;
        }
        return true;
    }
}
