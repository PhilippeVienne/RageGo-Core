package com.ragego.gui.console;

import com.ragego.engine.*;

import java.util.Scanner;

/**
 * Go Game interface on console for debug
 */
public class ConsoleMain implements TurnListener{

    private GameBoard board;
    private HumanPlayer humanOne;
    private HumanPlayer humanSecond;

    public ConsoleMain(){
        humanOne = new HumanPlayer("Player 1", this);
        humanSecond = new HumanPlayer("Player 2", this);
        board = new GameBoard(humanOne,humanSecond);
    }

    public void play(){
        try {
            printBoard(board);
            System.out.println("On joue ?");
            board.nextMove();
            play();
        } catch (Exception e){
            System.err.println("Il est mort, Jim.");
        }
    }

    private void printBoard(GameBoard board){
        final int[][] data = board.getRepresentation();
        StringBuilder builder = new StringBuilder("    ");
        for(int i=1;i<=data.length;i++){
            builder.append(' ').append((char) (64 + i)).append(' ');
        }
        builder.append('\n');
        for (int i = 0; i < data.length; i++) {
            int[] line = data[i];
            if(i<9){
                builder.append(' ');
            } else {
                builder.append(" ");
            }
            builder.append(i+1);
            if(i<9){
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
        for(int i=1;i<=data.length;i++){
            builder.append(' ').append((char)(64+i)).append(' ');
        }
        System.out.println("Le jeu :");
        System.out.println(builder.toString());
    }

    public static void main(String... args){
        new ConsoleMain().play();
    }

    @Override
    public void newTurn(GameBoard board, Player player) {
        System.out.println(player.getDisplayName()+", à ton tour");
        Intersection intersection;
        boolean play;
        do{
            intersection = readIntersection(board);
            if(intersection.getLine() < 0 || intersection.getColumn() < 0 ||
                    intersection.getColumn() >= board.getBoardSize() || intersection.getLine() >= board.getBoardSize()) {
                System.out.println("Euh le Goban fait " + board.getBoardSize() + " de taille, donne des coordonées à l'intérieur.");
                play = false;
            } else {
                try {
                    play = board.canPlay(player, intersection);
                } catch (GoRuleViolation goRuleViolation) {
                    System.out.println("Tu violes une des règle du Go !");
                    play = false;
                }
            }
            if(!play)
                System.out.println("Tu ne peux pas jouer sur cette case");
        } while (!play);
        final Stone element = new Stone();
        element.setBoard(board);
        element.setPlayer(player);
        board.setElement(intersection,element);
    }

    private Intersection readIntersection(GameBoard board) {
        try {
            System.out.print("Tu veux jouer sur (écris sous forme colonne-ligne) : ");
            String values = new Scanner(System.in).nextLine();
            if(!values.matches("[A-Z][0-9]+"))
                throw new Exception("Not good format");
            return new Intersection(((int)(values.split("[0-9]",2)[0].charAt(0)))-65, Integer.parseInt(values.split("[A-Z]",2)[1])-1, board);
        } catch(Exception e){
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
