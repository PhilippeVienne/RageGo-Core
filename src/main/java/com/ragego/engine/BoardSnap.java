package com.ragego.engine;

import java.util.Arrays;

/**
 * A board snap is an edit less situation of the game.
 * A board snap is useful to compare or store two game states.
 */
public class BoardSnap {

    private int[][] data;
    private String dataAsString;

    public BoardSnap(GameBoard board){
        if(board == null)
            throw new IllegalArgumentException("Board can not be null");
        data = board.getRepresentation();
        updateString();
    }

    public BoardSnap(int[][] representation) {
        data = Arrays.copyOf(representation,representation.length);
        updateString();
    }

    private void updateString(){
        StringBuilder builder = new StringBuilder();
        for (int[] column : data) {
            for (int i : column) {
                builder.append(i);
            }
        }
        dataAsString = builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof BoardSnap) && (dataAsString != null) && (((BoardSnap) obj).dataAsString != null) && dataAsString.equals(((BoardSnap) obj).dataAsString));
    }
}
