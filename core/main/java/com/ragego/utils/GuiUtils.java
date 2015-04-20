package com.ragego.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Provides useful methods for the gui package.
 */
public class GuiUtils {
    //Constants (useful for screen/map coordinates conversion) for alternate method
    private static final double PI_OVER_SIX = Math.PI/6;
    private static final double PI_OVER_THREE = Math.PI / 3;

    /**
     * Projects world coordinates into isometric coordinates through the solution of a triangle.
     * @param worldCoords The position in world coordinates
     * @param tileWidthHalf Half the width of the tiles, in world coordinates
     * @param tileHeightHalf Half the height of the tiles, in world coordinates
     * @param yOffset The offset on the world y-axis between the world's origin and the layer's grid origin
     * @param mapHeight Height of the map.
     * @return The coordinate corresponding to the position of the cell in the isometric coordinate system (top -&gt; down)
     */
    public static Vector2 worldToIsoLeft(Vector3 worldCoords, float tileWidthHalf, float tileHeightHalf, int mapHeight, float yOffset) {
        //Adjusts the position of the map's origin
        float y = worldCoords.y - yOffset;
        float x = worldCoords.x;

        // Projects and norms the world coordinates on the isometric coordinate systems's axes (left -> right)
        Vector2 isoCoords = new Vector2();
        isoCoords.x = (float)(0.5 * ((worldCoords.x / tileWidthHalf) - (worldCoords.y / tileHeightHalf)));
        isoCoords.y = (float)(0.5 * ((worldCoords.y/tileHeightHalf) + (worldCoords.x / tileWidthHalf)));

        // Casts the isometric coordinates
        isoCoords.x = Math.round(isoCoords.x);
        isoCoords.y = Math.round(isoCoords.y - 1);

        return isoCoords;
    }

    /**
     * Projects world coordinates into isometric tiles coordinates through the solution of a triangle.
     * @param worldCoords The position in world coordinates
     * @param tileWidthHalf Half the width of the tiles, in world coordinates
     * @param tileHeightHalf Half the height of the tiles, in world coordinates
     * @param yOffset The offset on the world y-axis between the world's origin and the layer's grid origin
     * @param mapHeight Height of the map.
     * @return The coordinate corresponding to the position of the cell in the isometric coordinate system (top -&gt; down)
     */
    public static Vector2 worldToIsoTop(Vector3 worldCoords, float tileWidthHalf, float tileHeightHalf, int mapHeight, float yOffset) {
        return isoLeftToIsoTop(worldToIsoLeft(worldCoords, tileWidthHalf, tileHeightHalf, mapHeight, yOffset), mapHeight);
    }

    public static Vector2 isoTopToIsoLeft(Vector2 isoVector, int mapHeight) {
        isoVector.y = mapHeight - isoVector.y - 1;
        return isoVector;
    }

    public static Vector2 isoLeftToIsoTop(Vector2 isoVector, int mapHeight) {
        //Rotates and translates the isometric coordinate system into top -> configuration
        isoVector.y = mapHeight - isoVector.y - 1;
        return isoVector;
    }

    /**
     * Projects world coordinates into isometric coordinates through the solution of a triangle.
     *
     * @param isoCoords      The position in the isometric coordinate system (top -&gt; down)
     * @param tileWidthHalf  Half the width of the tiles, in world coordinates
     * @param tileHeightHalf Half the height of the tiles, in world coordinates
     * @param yOffset        The offset on the world y-axis between the world's origin and the layer's grid origin.
     * @param mapHeight      Height of the map.
     * @return The coordinate corresponding to the position of the cell's center in the world coordinate system
     */
    public Vector2 isoToWorld (Vector2 isoCoords, float tileWidthHalf, float tileHeightHalf, int mapHeight, float yOffset){
        //Rotates and translates the isometric coordinate system so that the map origin is at the world's origin
        isoCoords.y = mapHeight - isoCoords.y;

        return new Vector2(tileWidthHalf * (isoCoords.x + isoCoords.y + 1),
                tileHeightHalf * (isoCoords.y - isoCoords.x));
    }

    /**
     * Projects world coordinates into isometric coordinates through the solution of a triangle.
     *
     * @param worldCoords The position in world coordinates
     * @param mapUnit The shared norm of the unit vectors associated with the axes
     * @param yOffset The offset on the world y-axis between the world's origin and the layer's grid origin
     * @return The coordinate corresponding to the position of the cell in the isometric coordinate system
     */
    @Deprecated
    public static Vector2 worldToIsoAlternate (Vector3 worldCoords, float mapUnit, float yOffset){
        //Adjust the position of the map's origin
        float y = worldCoords.y - yOffset;
        float x = worldCoords.x;

        System.out.println("World coordinates adjusted : "
                + "X: " + x + " Y: " + y);

        // Computes triangulation values : side of the triangle and two angles
        double vectorNorm = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double alpha = PI_OVER_SIX - Math.atan(y/x);
        double beta = PI_OVER_THREE - alpha;

        System.out.println("Triangulation values : "
                + "vectorNorm: " + vectorNorm + " beta: " + beta + " alpha: " + alpha);

        // Projects, norms and casts the world coordinates on the isometric coordinate systems's axes
        Vector2 isoCoords = new Vector2((float)(vectorNorm*Math.sin(alpha)/Math.sin(alpha+beta)),
                (float)(vectorNorm*Math.sin(beta)/Math.sin(alpha+beta)));

        System.out.println("Iso coordinates projected: "
                + "X: " + isoCoords.x + " Y: " + isoCoords.y);

        isoCoords.x = (int)(isoCoords.x/mapUnit);
        isoCoords.y = (int)(isoCoords.y/mapUnit);

        return isoCoords;
    }
}
