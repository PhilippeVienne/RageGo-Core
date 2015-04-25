package com.ragego.engine;

import java.util.ArrayList;

/**
 * Mark points on a Go board.
 */
public class Marker {
    private final int m_size;
    private final GameBoard m_board;
    private boolean m_mark[][];

    /**
     * Constructor.
     *
     * @param size Size of the board.
     */
    public Marker(int size, GameBoard board) {
        m_size = size;
        m_board = board;
        m_mark = new boolean[size][size];
    }

    /**
     * Clear all marked points.
     */
    public void clear() {
        for (int x = 0; x < m_size; ++x)
            for (int y = 0; y < m_size; ++y) {
                final Intersection intersection = Intersection.get(x, y, m_board);
                m_mark[intersection.getColumn()][intersection.getLine()] = false;
            }
    }

    /**
     * Clear a marked point.
     *
     * @param p The point to clear.
     */
    public void clear(Intersection p) {
        m_mark[p.getColumn()][p.getLine()] = false;
    }

    /**
     * Clear all points from a list.
     *
     * @param points List of points.
     */
    public void clear(ArrayList<Intersection> points) {
        for (Intersection point : points) {
            clear(point);
        }
    }

    /**
     * Check if a point is marked.
     *
     * @param p The point to check.
     * @return true, if point is marked, false otherwise.
     */
    public boolean get(Intersection p) {
        return m_mark[p.getColumn()][p.getLine()];
    }

    /**
     * Check if no point is marked.
     *
     * @return true, if no point is marked, false otherwise.
     */
    public boolean isCleared() {
        for (int x = 0; x < m_size; ++x)
            for (int y = 0; y < m_size; ++y)
                if (m_mark[x][y])
                    return false;
        return true;
    }

    /**
     * Mark a point.
     *
     * @param p The point to mark.
     */
    public void set(Intersection p) {
        set(p, true);
    }

    /**
     * Mark or clear a point.
     *
     * @param p     The point to mark or clear.
     * @param value true, if point should be marked; false, if point should
     *              be cleared.
     */
    public void set(Intersection p, boolean value) {
        m_mark[p.getColumn()][p.getLine()] = value;
    }

    /**
     * Mark all points from a list.
     *
     * @param points List of points.
     */
    public void set(ArrayList<Intersection> points) {
        for (Intersection point : points) {
            set(point);
        }
    }
}
