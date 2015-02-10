package com.ragego.engine;

import java.util.ArrayList;

/**
 * Shape is the configuration of stones in their flexibility and efficiency
 * at staying connected, forming eyes, and maintaining liberties.
 * <p>
 *     Stones are
 * said to have good shape if they are efficient and flexible, or bad shape
 * if they are inefficient. Classic examples of good shape are the ponnuki
 * (four stones in a diamond created by capturing an enemy stone) and the
 * bamboo joint (a 2x3 pattern of two stones, two spaces and two more stones).
 * Examples of bad shape are the empty triangle (three adjacent stones forming
 * an 'L') and the dango (large clump of stones not containing any eyes).
 * Joseki is, in large part, the study of forming good shapes with the stones.
 * </p>
 * <a href="http://en.wikipedia.org/wiki/Shape_%28Go%29">See Wikipedia</a>
 * @author Philippe Vienne
 */
public class Shape implements GoElement{

    @Override
    public ArrayList<Intersection> getPositions() {
        return null;
    }
}
