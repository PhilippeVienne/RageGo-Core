package com.ragego.engine;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        byte[] bytes = new byte[data.length*data[0].length];
        for (int i1 = 0, dataLength = data.length; i1 < dataLength; i1++) {
            int[] line = data[i1];
            for (int i2 = 0, columnLength = line.length; i2 < columnLength; i2++) {
                int i = line[i2];
                bytes[i1*dataLength+i2] = (byte) i;
            }
        }
        try {
            dataAsString = new String(MessageDigest.getInstance("MD5").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof BoardSnap) && (dataAsString != null) && (((BoardSnap) obj).dataAsString != null) && dataAsString.equals(((BoardSnap) obj).dataAsString));
    }
}
