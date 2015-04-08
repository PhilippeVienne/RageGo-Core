package com.ragego;

import com.ragego.engine.*;

import java.util.Iterator;
import java.util.Scanner;

/**
 * Go Game interface on console for debug
 */
public class ConsoleMain implements TurnListener {

    private GameBoard board;
    private HumanPlayer humanOne;
    private HumanPlayer humanSecond;

    public ConsoleMain() {
        humanOne = new HumanPlayer("Player 1", this);
        humanSecond = new HumanPlayer("Player 2", this);
        board = new GameBoard(humanOne, humanSecond);
    }

    public void play() {
        try {
            printBoard(board);
            System.out.println("On joue ?");
            board.nextMove();
            play();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            //System.err.println("Il est mort, Jim.");
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

    public static void main(String... args) {
        new ConsoleMain().play();
    }

    @Override
    public void newTurn(GameBoard board, Player player) {
        System.out.println(player.getDisplayName() + ", à ton tour");
        Intersection intersection;
        GameNode node = null;
        while(node==null) {
            intersection = readIntersection(board);
            if (!board.isValidIntersection(intersection)) {
                System.out.println("Euh le Goban fait " + board.getBoardSize() + " de taille, donne des coordonées à l'intérieur.");
                node = null;
            } else {
                try {
                    node = new GameNode(board,null, GameNode.Action.PUT_STONE,intersection,player);
                    if(!board.canPlay(node)) {
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

    private Iterator<String> coupsIterator = new Iterator<String>() {

        private String[] coups = {"G18","G17","A16","G19","B16","F18","C16","H18"};
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
    };

    private Intersection readIntersection(GameBoard board) {
        try {
            System.out.print("Tu veux jouer sur (écris sous forme colonne-ligne) : ");
            String values = new Scanner(System.in).nextLine();
            if(coupsIterator.hasNext()){
                values = coupsIterator.next();
                System.out.println("On joue : "+values);
            }
            if (!values.matches("[A-Z][0-9]+"))
                throw new Exception("Not good format");
            return new Intersection(((int) (values.split("[0-9]", 2)[0].charAt(0))) - 65, Integer.parseInt(values.split("[A-Z]", 2)[1]) - 1, board);
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