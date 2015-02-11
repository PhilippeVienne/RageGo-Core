package com.ragego.engine;

/**
 * Generate Fake valid or invalid data for test.
 * Add as many generators you need to this class. But use them only for test.
 * All methods should be static.
 */
public class FakeGenerator {

    /**
     * Create a board with minimal data
     * @return A usable board
     */
    public static GameBoard generateSimpleBoard() {
        return new GameBoard();
    }

    /**
     * Create an intersection from specific data
     * @see com.ragego.engine.Intersection#(int,int,com.ragego.engine.GameBoard)
     */
    public static Intersection generateIntersection(int column, int line, GameBoard board) {
        return new Intersection(column,line,board);
    }

    public static Intersection generateNeighbourgIntersection(Intersection i1) {
        switch (((int)(Math.random()*4))){
            case 0:
                return generateIntersection(i1.getColumn()-1,i1.getLine(),i1.getBoard());
            case 1:
                return generateIntersection(i1.getColumn()+1,i1.getLine(),i1.getBoard());
            case 2:
                return generateIntersection(i1.getColumn(),i1.getLine()-1,i1.getBoard());
            case 3:
            default:
                return generateIntersection(i1.getColumn(),i1.getLine()+1,i1.getBoard());
        }
    }

    public static Intersection generateNotNeighbourgIntersection(Intersection i1) {
        switch (((int)(Math.random()*4))){
            case 0:
                return generateIntersection(i1.getColumn()-1,i1.getLine()-1,i1.getBoard());
            case 1:
                return generateIntersection(i1.getColumn()+1,i1.getLine()+1,i1.getBoard());
            case 2:
                return generateIntersection(i1.getColumn()-1,i1.getLine()-1,i1.getBoard());
            case 3:
            default:
                return generateIntersection(i1.getColumn()+1,i1.getLine()+1,i1.getBoard());
        }
    }
}
