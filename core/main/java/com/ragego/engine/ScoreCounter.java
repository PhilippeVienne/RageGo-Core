package com.ragego.engine;

import java.util.ArrayList;

/**
 * Counter for player scores in the game.
 * This function manage score between players during a game and help to determine if a party is ended.
 * @author Philippe Vienne
 * @since 1.0
 */
public class ScoreCounter implements GameListener {

    public final static double KOMI = 5.5;


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
            if (!mark.get(p)) {
                territory.clear();
                if (isTerritory(mark, p, territory, board.getBlackPlayer()))
                    setScore(territory, board.getBlackPlayer());
                else {
                    mark.clear(territory);
                    if (isTerritory(mark, p, territory, board.getWhitePlayer()))
                        setScore(territory, board.getWhitePlayer());
                    else
                        mark.clear(territory);
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
            // Black player is in a white area so he is captivated
            if (playerOnIntersection == board.getBlackPlayer() && playerWhoOwnIntersection == board.getWhitePlayer()) {
                ++capturedBlack;
                ++territoryWhite;
                --territoryDiff;
            }
            // White player is in a black area so he is captivated
            if (playerOnIntersection == board.getWhitePlayer() && playerWhoOwnIntersection == board.getBlackPlayer()) {
                ++capturedWhite;
                ++territoryBlack;
                ++territoryDiff;
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
        if (playerOnIntersection == board.getOpponent(player) && mark.get(p)) {
            final Stone element = board.getElement(p);
            return deadStones.contains(element);
        }
        if (playerOnIntersection != null && playerOnIntersection.equals(player))
            return true;
        if (mark.get(p))
            return true;
        if (playerOnIntersection == board.getOpponent(player)) { // We have to discuss if it's a dead stone
            return board.getElement(p) != null && isADeadStone(p);
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
        for (Intersection neighbour : p.getEightNeighbours()) {
            final Stone stone = board.getElement(neighbour);
            if (stone != null && stone.getPlayer() == element.getPlayer() && !deadMarker.get(p)) {
                aDeadStone = isADeadStone(stone.getPosition());
                if (aDeadStone) break;
            }
            if (opponentInfluences[neighbour.getColumn()][neighbour.getLine()] > influence) {
                aDeadStone = true;
                break;
            }
        }
        if (aDeadStone) {
            deadStones.add(element);
        } else {
            notDeadStones.add(element);
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
