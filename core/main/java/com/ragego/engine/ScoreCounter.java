package com.ragego.engine;

import java.util.ArrayList;

/**
 * Counter for player scores in the game.
 * This function manage score between players during a game and help to determine if a party is ended.
 * @author Philippe Vienne
 * @since 1.0
 */
public class ScoreCounter implements GameListener {

    public final static double DEFAULT_KOMI = 6.5;

    protected int areaBlack;

    protected int areaWhite;

    protected double result;

    protected double resultArea;

    protected double resultTerritory;

    protected int territoryBlack;

    protected int territoryWhite;
    InfluenceAlgorithm influence = null;
    Marker deadMarker;
    /**
     * Board where the score is being counted.
     */
    private GameBoard board;
    /**
     * Contain score for each players.
     */
    private Player[][] score;
    private ScoringMethod rules;
    private Komi komi;
    private Integer capturedBlack = 0;
    private Integer capturedWhite = 0;
    private ArrayList<Stone> deadStones = new ArrayList<Stone>();
    private ArrayList<Stone> notDeadStones = new ArrayList<Stone>();

    public ScoreCounter(GameBoard board) {
        this.board = board;
        board.addGameListener(this);
    }

    public static String formatResult(double result) {
        long intResult = Math.round(result * 2);
        String strResult;
        if (intResult % 2 == 0)
            strResult = Long.toString(intResult / 2);
        else
            strResult = Long.toString(intResult / 2) + ".5";
        if (intResult > 0)
            return "B+" + strResult;
        else if (intResult < 0)
            return "W+" + (-result);
        else
            return "0";
    }

    public String formatResult() {
        return formatResult(result);
    }

    public void updateRules(ScoringMethod rules) {
        this.rules = rules;
        if (rules == ScoringMethod.TERRITORY)
            result = resultTerritory;
        else {
            result = resultArea;
        }
    }

    @Override
    public void newStoneAdded(Stone stone) {
        // No consequences on score
    }

    @Override
    public void stoneRemoved(Stone stone) {
        if (stone.getPlayer() == board.getBlackPlayer()) {
            capturedBlack++;
        } else if (stone.getPlayer() == board.getWhitePlayer()) {
            capturedWhite++;
        }
    }

    @Override
    public void playNode(GameNode node) {
        // No consequences on score
    }

    @Override
    public void newTurn(GameBoard board, Player player) {
        // No consequences on score
    }

    @Override
    public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {
        // No consequences on score
    }

    @Override
    public void startOfTurn(GameBoard board, Player player, Player previousPlayer) {
        // No consequences on score
    }

    private void clearScore() {
        score = new Player[board.getBoardSize()][board.getBoardSize()];
    }

    /**
     * Update score after changing the life-death status of stones.
     */
    public void compute() {
        clearScore();
        Marker mark = new Marker(board.getBoardSize(), board);
        Marker visitedMark = new Marker(board.getBoardSize(), board);
        boolean allEmpty = true;
        for (Intersection p : board.getBoardIntersections()) {
            final Player playerOn = board.getPlayerOn(p);
            setScore(p, playerOn);
            if (allEmpty && board.getElement(p) != null)
                allEmpty = false;
        }
        if (allEmpty)
            return;
        clearScore();
        deadStones.clear();
        notDeadStones.clear();
        deadMarker = new Marker(board.getBoardSize(), board);
        influence = new InfluenceAlgorithm(board);
        ArrayList<Intersection> territory = new ArrayList<Intersection>();
        for (Intersection p : board.getBoardIntersections()) {
            if (!visitedMark.get(p)) {
                territory.clear();
                mark.clear();
                if (isTerritory(mark, p, territory, board.getBlackPlayer())) {
                    setScore(linkedDeadStones(new Marker(board.getBoardSize(), board), territory, board.getBlackPlayer()), board.getBlackPlayer());
                    visitedMark.set(territory);
                }
                else {
                    mark.clear();
                    if (isTerritory(mark, p, territory, board.getWhitePlayer())) {
                        setScore(linkedDeadStones(new Marker(board.getBoardSize(), board), territory, board.getWhitePlayer()), board.getWhitePlayer());
                        visitedMark.set(territory);
                    }
                    else
                        mark.clear();
                }
            }
        }
        String[] lines = new String[score.length];
        for (int i = 0; i < lines.length; i++) {
            final String s = String.valueOf(i);
            lines[i] = " " + s;
            switch (s.length()) {
                case 1:
                    lines[i] += "  ";
                    break;
                case 2:
                default:
                    lines[i] += " ";
            }
        }
        for (Player[] aScore : score) {
            for (int i1 = 0; i1 < aScore.length; i1++) {
                Player anAScore = aScore[i1];
                lines[i1] += board.getLetterForPlayer(anAScore);
                lines[i1] += "  ";
            }
        }
        for (String line : lines) {
            System.out.println(line);
        }
    }

    private ArrayList<Intersection> linkedDeadStones(Marker marker, ArrayList<Intersection> territory, Player player) {
        for (Intersection territoryInt : territory) {
            for (Intersection borders : territoryInt.getNeighboursIntersections()) {
                if (territory.contains(borders)) continue;
                if (marker.get(borders)) continue;
                marker.set(borders);
                if (board.getPlayerOn(borders) == board.getOpponent(player) && isADeadStone(borders)) {
                    territory.add(borders);
                    deadStones.add(board.getElement(borders));
                    return linkedDeadStones(marker, territory, player);
                }
            }
        }
        return territory;
    }

    /**
     * Get the score.
     *
     * @param komi  The komi.
     * @param rules The scoring method
     */
    public ScoreCounter getScore(Komi komi, ScoringMethod rules) {
        this.rules = rules;
        this.komi = komi;
        int areaDiff = 0;
        int territoryDiff = 0;
        for (Intersection p : board.getBoardIntersections()) {
            Player playerOnIntersection = board.getPlayerOn(p);
            Player playerWhoOwnIntersection = getScoredPlayer(p);
            if (playerWhoOwnIntersection == board.getBlackPlayer()) {
                ++areaBlack;
                ++areaDiff;
            } else if (playerWhoOwnIntersection == board.getWhitePlayer()) {
                ++areaWhite;
                --areaDiff;
            }
            if (playerOnIntersection == null) { // We have no stone on this intersection
                if (playerWhoOwnIntersection == board.getBlackPlayer()) { // Territory is owned by blacks
                    ++territoryBlack; // Increase his score
                    ++territoryDiff;
                } else if (playerWhoOwnIntersection == board.getWhitePlayer()) { // owned by whites
                    ++territoryWhite; // Increase white's score
                    --territoryDiff;
                }
            }
            if (board.getElement(p) != null && deadStones.contains(board.getElement(p))) {
                if (playerWhoOwnIntersection == board.getBlackPlayer()) { // Territory is owned by blacks
                    ++capturedWhite;
                } else if (playerWhoOwnIntersection == board.getWhitePlayer()) { // owned by whites
                    ++capturedBlack;
                }
            }
        }
        resultArea = areaDiff;
        resultTerritory =
                capturedWhite - capturedBlack + territoryDiff;
        if (komi != null) {
            resultArea -= komi.toDouble();
            resultTerritory -= komi.toDouble();
        }
        if (rules == ScoringMethod.TERRITORY)
            result = resultTerritory;
        else {
            result = resultArea;
        }
        return this;
    }

    private Player getScoredPlayer(Intersection p) {
        return score[p.getColumn()][p.getLine()];
    }

    private boolean isTerritory(Marker mark, Intersection p,
                                ArrayList<Intersection> territory, Player player) {
        Player playerOnIntersection = board.getPlayerOn(p);
        if (playerOnIntersection != null && playerOnIntersection.equals(player))
            return true;
        if (mark.get(p))
            return true;
        if (playerOnIntersection == board.getOpponent(player)) { // We have to discuss if it's a dead stone
            if (!(board.getElement(p) != null && isADeadStone(p)))
                return false;
            else {
                for (Intersection intersection : p.getEightNeighbours()) {
                    if (territory.contains(p)) {
                        mark.set(p);
                        territory.add(p);
                        return true;
                    }
                }
                return true;
            }
        }
        mark.set(p);
        territory.add(p);
        for (Intersection adj : p.getNeighboursIntersections())
            if (!isTerritory(mark, adj, territory, player))
                return false;
        return true;
    }

    private boolean isADeadStone(Intersection p) {
        deadMarker.set(p);
        if (influence == null) return false;
        final Stone element = board.getElement(p);
        if (element == null) return false;
        if (notDeadStones.contains(element)) return false;
        if (getScoredPlayer(p) == element.getPlayer()) return false;
        boolean aDeadStone = false;
        double[][] influences;
        double[][] opponentInfluences;
        if (board.getBlackPlayer() == element.getPlayer()) {
            influences = influence.getBlackStrength();
            opponentInfluences = influence.getWhiteStrength();
        } else {
            influences = influence.getWhiteStrength();
            opponentInfluences = influence.getBlackStrength();
        }
        double influence = influences[p.getColumn()][p.getLine()];
        final double opponent = opponentInfluences[p.getColumn()][p.getLine()];
        if (opponent - influence > 400) {
            return true;
        } else if (influence - opponent > 2000) {
            return false;
        }
        for (Intersection neighbour : p.getEightNeighbours()) {
            final Stone stone = board.getElement(neighbour);
            if (opponentInfluences[neighbour.getColumn()][neighbour.getLine()] - influence > 400) {
                aDeadStone = true;
                break;
            }
            if (board.isEmpty(neighbour)) {
                for (Intersection otherIntersection : neighbour.getNeighboursIntersections()) {
                    if (opponentInfluences[otherIntersection.getColumn()][otherIntersection.getLine()] - influence > 400) {
                        aDeadStone = true;
                        break;
                    }
                }
            }
        }
        return aDeadStone;
    }

    private void setScore(Intersection p, Player c) {
        score[p.getColumn()][p.getLine()] = c;
    }

    private void setScore(ArrayList<Intersection> points, Player c) {
        for (Intersection p : points)
            setScore(p, c);
    }

    public enum ScoringMethod {
        AREA, TERRITORY
    }
}
