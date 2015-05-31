package com.ragego.gui.objects;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.ragego.engine.Intersection;
import com.ragego.engine.Stone;

/**
 * Represents a {@link Stone} on the Goban.
 */
public class GraphicStone {

    private final Stone stone;
    private final Goban goban;
    private final TiledMapTile stoneTile;

    public GraphicStone(Stone stone, Goban goban) {
        this.stone = stone;
        this.goban = goban;
        this.stoneTile = generateTile();
    }

    private TiledMapTile generateTile() {
        if ((stone != null && stone.getPlayer() != null) && (goban != null && goban.getBoard() != null)) {
            if (goban.getBoard().getLetterForPlayer(stone.getPlayer()) == 'B') {
                return goban.getBlackStoneTile();
            } else return goban.getWhiteStoneTile();
        } else {
            return null;
        }
    }


    public Intersection getIntersection() {
        return stone.getPosition();
    }

    public TiledMapTile getStoneTile() {
        return stoneTile;
    }

    public Stone getStone() {
        return stone;
    }
}
