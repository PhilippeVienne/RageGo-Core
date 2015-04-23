package com.ragego;

import com.ragego.engine.*;

import java.util.Iterator;
import java.util.Scanner;

/**
 * Go Game interface on console for debug
 */
public class ConsoleMain implements TurnListener {

    private GameBoard board;
    private Iterator<String> coupsIterator = new Iterator<String>() {

        private String[] coups = {"G18", "G17", "A16", "G19", "B16", "F18", "C16", "H18"};
        private int position = 0;

        @Override
        public boolean hasNext() {
            return position < coups.length;
        }

        @Override
        public String next() {
            int index = position;
            position++;
            return coups[index];
        }

        @Override
        public void remove() {

        }
    };

    public ConsoleMain() {
        HumanPlayer humanOne = new HumanPlayer("Player 1", this);
        HumanPlayer humanSecond = new HumanPlayer("Player 2", this);
        board = new GameBoard(humanOne, humanSecond);
    }

    public static void main(String... args) {
        new ConsoleMain().play();
    }

    @SuppressWarnings("InfiniteRecursion") // This is wanted
    public void play() {
        try {
            printBoard(board);
            System.out.println("On joue ?");
            board.nextMove();
            play();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void printBoard(GameBoard board) {
        final int[][] data = board.getRepresentation();
        StringBuilder builder = new StringBuilder("    ");
        for (int i = 1; i <= data.length; i++) {
            builder.append(' ').append((char) (64 + i)).append(' ');
        }
        builder.append('\n');
        for (int i = 0; i < data.length; i++) {
            int[] line = data[i];
            if (i < 9) {
                builder.append(' ');
            } else {
                builder.append(" ");
            }
            builder.append(i + 1);
            if (i < 9) {
                builder.append(' ');
            }
            builder.append(' ');
            for (int i1 : line) {
                builder.append(' ');
                builder.append(i1);
                builder.append(' ');
            }
            builder.append('\n');
        }
        builder.append("    ");
        for (int i = 1; i <= data.length; i++) {
            builder.append(' ').append((char) (64 + i)).append(' ');
        }
        System.out.println("Le jeu :");
        System.out.println(builder.toString());
    }

    @Override
    public void newTurn(GameBoard board, Player player) {
        System.out.println(player.getDisplayName() + ", à ton tour");
        Intersection intersection;
        GameNode node = null;
        while (node == null) {
            intersection = readIntersection(board);
            if (!board.isValidIntersection(intersection)) {
                System.out.println("Euh le Goban fait " + board.getBoardSize() + " de taille, donne des coordonées à l'intérieur.");
                node = null;
            } else {
                try {
                    node = new GameNode(board, null, GameNode.Action.PUT_STONE, intersection, player);
                    if (!board.canPlay(node)) {
                        System.out.println("Tu ne peux pas jouer sur cette case");
                        node = null;
                    }
                } catch (GoRuleViolation goRuleViolation) {
                    System.out.println("Tu violes une des règle du Go !");
                    node = null;
                }
            }
        }
        board.play(node);
    }

    private Intersection readIntersection(GameBoard board) {
        if (coupsIterator.hasNext()) {
            final String next = coupsIterator.next();
            System.out.println("Prochain coup : " + next + ", appuyez sur entrée pour valider.");
            new Scanner(System.in).nextLine();
            return Intersection.get(next, board);
        }
        try {
            System.out.print("Tu veux jouer sur (écris sous forme colonne-ligne) : ");
            String values = new Scanner(System.in).nextLine();
            return Intersection.get(values, board);
        } catch (Exception e) {
            System.out.println("Ecris bien sous la forme \"A5\" où A est la colonne et 5 est la ligne !");
            return readIntersection(board);
        }
    }

    @Override
    public void endOfTurn(GameBoard board, Player player, Player nextPlayer) {

    }

    @Override
    public void startOfTurn(GameBoard board, Player player, Player previousPlayer) {

    }
}