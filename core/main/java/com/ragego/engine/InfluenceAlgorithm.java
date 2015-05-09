package com.ragego.engine;

/**
 * The class compute data by the influence algorithm.
 * <h2>The Influence Algorithm</h2>
 * <p/>
 * <p>Let (m, n) be the coordinates of the influence source and (i, j) the coordinates of a an intersection being
 * visited during propagation, using the same notation as in the accumulate_influence() function. Influence is now
 * propagated to its eight closest neighbors, including the diagonal ones, according to the follow scheme:</p>
 * <p>For each of the eight directions (di, dj), do:</p>
 * <ol>
 * <li>Compute the scalar product di*(i-m) + dj*(j-n) between the vectors (di,dj) and (i,j) - (m,n)</li>
 * <li>If this is negative or zero, the direction is not outwards and we continue with the next direction.
 * The exception is when we are visiting the influence source, i.e. the first intersection, when we spread
 * influence in all directions anyway.</li>
 * <li>If (i+di, j+dj) is outside the board or occupied we also continue with the next direction.</li>
 * <li>Let S be the strength of the influence at (i, j). The influence propagated to (i+di, j+dj) from this
 * intersection is given by P*(1/A)*D*S, where the three different kinds of damping are:
 * <li>The permeability ‘P’, which is a property of the board intersections. Normally this is one, i.e. unrestricted
 * propagation, but to stop propagation through e.g. one step jumps, the permeability is set to zero at such
 * intersections through pattern matching. This is further discussed below.</li>
 * <li>The attenuation ‘A’, which is a property of the influence source and different in different directions.
 * By default this has the value 3 except diagonally where the number is twice as much. By modifying the attenuation
 * value it is possible to obtain influence sources with a larger or a smaller effective range.</li>
 * <li>The directional damping ‘D’, which is the squared cosine of the angle between (di,dj) and (i,j) - (m,n).
 * The idea is to stop influence from "bending" around an interfering stone and get a continuous behavior at the
 * right angle cutoff. The choice of the squared cosine for this purpose is rather arbitrary, but has the advantage
 * that it can be expressed as a rational function of ‘m’, ‘n’, ‘i’, ‘j’, ‘di’, and ‘dj’, without involving any trigonometric
 * or square root computations. When we are visiting the influence source we let by convention this factor be one.</li>
 * </ol>
 * <p>Influence is typically contributed from up to three neighbors "between" this intersection and the influence
 * source. These values are simply added together. As pointed out before, all contributions will automatically have
 * been made before the intersection itself is visited.</p>
 */
public class InfluenceAlgorithm {

    private final int board_size;
    private final GameBoard board;
    private double[][] strength;
    private double[][] whiteStrength;
    private double[][] blackStrength;
    private boolean[][] flags;

    public InfluenceAlgorithm(GameBoard board) {
        this.board = board;
        board_size = board.getBoardSize();
        strength = initArrayTo1Values(board_size);
        whiteStrength = initArrayTo1Values(board_size);
        blackStrength = initArrayTo1Values(board_size);
        for (Stone stone : board.getStones()) {
            computeRecursivelyOnBoard(stone, null, strength);
            computeRecursivelyOnBoard(stone, null, stone.getPlayer() == board.getWhitePlayer() ? whiteStrength : blackStrength);
        }
    }

    public double[][] getStrength() {
        return strength;
    }

    public double[][] getWhiteStrength() {
        return whiteStrength;
    }

    public double[][] getBlackStrength() {
        return blackStrength;
    }

    /**
     * Create a squared two-dim array with 1 as value.
     *
     * @param size The wanted size
     * @return The new array.
     */
    public double[][] initArrayTo1Values(int size) {
        double[][] a = new double[size][size];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                a[i][j] = getStrengthOn(i, j, a);
            }
        }
        return a;
    }

    private void computeRecursivelyOnBoard(Stone source, Intersection visiting, double[][] strength) {
        if (visiting != null) {
            flags[visiting.getColumn()][visiting.getLine()] = true;
            computeInfluence(source, visiting, strength);
        } else {
            flags = new boolean[board_size][board_size];
            visiting = source.getPosition();
        }
        for (Intersection intersection : visiting.getNeighboursIntersections()) {
            if (!flags[intersection.getColumn()][intersection.getLine()])
                computeRecursivelyOnBoard(source, intersection, strength);
        }
    }

    private void computeInfluence(Stone source, Intersection visited, double[][] strength) {
        final double m = source.getPosition().getColumn() + 1, n = source.getPosition().getLine() + 1,
                i = visited.getColumn() + 1, j = visited.getLine() + 1;
        for (Intersection dIntersection : visited.getEightNeighbours()) {
            final double di = dIntersection.getColumn() + 1, dj = dIntersection.getLine() + 1;
            double scalarProduct = (di) * (i - m) + (dj) * (j - n);
            if (scalarProduct <= 0) continue;
            strength[((int) di - 1)][((int) dj - 1)] +=
                    (getPermeability(dIntersection, source) / getAttenuation(source, dIntersection)) *
                            (((i - m) * di + (j - n) * dj) * ((i - m) * di + (j - n) * dj) / ((di * di + dj * dj) * ((i - m) * (i - m) + (j - n) * (j - n))))
                            * getStrengthOn(dIntersection, strength)
            //* strength[((int) (i - 1))][((int) (j - 1))]
            ;
        }
    }

    private double getAttenuation(Stone source, Intersection dIntersection) {
        Intersection si = source.getPosition();
        double value;
        if (si.getLine() == dIntersection.getLine() || si.getColumn() == dIntersection.getColumn())
            value = 1;
        else
            value = 2;
        value *= source.getPosition().distanceTo(dIntersection);
        if (value == 0) return 1;
        return value;
    }

    private double getPermeability(Intersection intersection, Stone source) {
        if (board.getElement(intersection) == null) {
            return 1;
        }
        return source.getPlayer() == board.getElement(intersection).getPlayer() ? 2 : 0.5;
    }

    private double getStrengthOn(Intersection intersection, double[][] strength) {
        return getStrengthOn(intersection.getColumn(), intersection.getLine(), strength);
    }

    private double getStrengthOn(double column, double line, double[][] strength) {
        return getStrengthOn((int) column, (int) line, strength);
    }

    private double getStrengthOn(int column, int line, double[][] strength) {
        if (column < 0 || column > strength.length || line < 0 || line > strength[column].length)
            return 0.0;
        else {
            double value = Math.min(Math.min(column * line, column * (board_size - line - 1)), Math.min((board_size - column - 1) * line, (board_size - line - 1) * (board_size - column - 1)));
            if (value < 4) {
                value = 4.0;
            }
            return value;
        }
    }


}
