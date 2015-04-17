package com.ragego.engine;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;

import java.util.LinkedList;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class GameBoardTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private GameBoard gameBoard;
    private TestPlayer firstPlayer;
    private TestPlayer secondPlayer;

    @Before
    public void init() {
        firstPlayer = new TestPlayer();
        secondPlayer = new TestPlayer();
        gameBoard = new GameBoard(firstPlayer, secondPlayer);
    }

    @Test
    public void playerShouldNotEditingBoard() {
        firstPlayer.setListener(new TestPlayerTurnListener(firstPlayer) {
            @Override
            public void startOfTurn(GameBoard board, Player player, Player previousPlayer) {
                board.play(new GameNode(gameBoard, null, GameNode.Action.PUT_STONE, Intersection.get("aa", gameBoard), firstPlayer));
            }
        });
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("A player has modified the board, and this should not be");
        gameBoard.nextMove();
    }

    @Test
    public void canPlayATurn() {
        firstPlayer.registerNodeToPlay(new GameNode(gameBoard, null, GameNode.Action.PUT_STONE, Intersection.get("aa", gameBoard), firstPlayer));
        gameBoard.nextMove();
        final Stone element = gameBoard.getElement(Intersection.get("aa", gameBoard));
        assertThat(element, is(not(nullValue())));
    }

    @Test
    public void canPlayTwoTurns() {
        final Intersection intersectionForFirstPlayer = Intersection.get("aa", gameBoard);
        firstPlayer.registerNodeToPlay(new GameNode(gameBoard, null, GameNode.Action.PUT_STONE, intersectionForFirstPlayer, firstPlayer));
        final Intersection intersectionForSecondPlayer = Intersection.get("ab", gameBoard);
        secondPlayer.registerNodeToPlay(new GameNode(gameBoard, null, GameNode.Action.PUT_STONE, intersectionForSecondPlayer, secondPlayer));
        gameBoard.nextMove();
        assertThat(gameBoard.getElement(intersectionForFirstPlayer), is(not(nullValue())));
        assertThat(gameBoard.getElement(intersectionForSecondPlayer), is(nullValue()));
        gameBoard.nextMove();
        assertThat(gameBoard.getElement(intersectionForFirstPlayer), is(not(nullValue())));
        assertThat(gameBoard.getElement(intersectionForSecondPlayer), is(not(nullValue())));
    }

    @Test
    public void canNotSuicide() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("violating a Go rule"));
        thrown.expectCause(ThrowableMessageMatcher.hasMessage(containsString("You can not kill yourself")));
        addTurnsToPlayer(firstPlayer, "a1", "c1", "b2");
        addTurnsToPlayer(secondPlayer, "a3", "b3", "b1");
        actPlay();
    }

    @Test
    public void canSuicideToCaptureStones() {
        addTurnsToPlayer(firstPlayer, "a1", "c1", "b2");
        addTurnsToPlayer(secondPlayer, "a2", "b3", "b1");
        actPlay();
    }

    @Test
    public void canNotMakeAKO() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("violating a Go rule"));
        addTurnsToPlayer(firstPlayer, "a1", "c1", "b2", "a1");
        addTurnsToPlayer(secondPlayer, "a2", "b3", "b1");
        actPlay();
    }

    @Test
    public void canNotPlayOnExistentStone() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("violating a Go rule"));
        addTurnsToPlayer(firstPlayer, "a1");
        actPlay();
        assertThat(gameBoard.getElement(Intersection.get(0, 0, gameBoard)), is(notNullValue()));
        addTurnsToPlayer(secondPlayer, "a1");
        gameBoard.nextMove();
    }

    /**
     * Play all turns registred by players.
     * This method call {@link GameBoard#nextMove()} until one player has no more moves to play.
     */
    private void actPlay() {
        while (firstPlayer.hasNextNode() || secondPlayer.hasNextNode()) {
            if (firstPlayer.hasNextNode()) {
                gameBoard.nextMove();
            } else break;
            if (secondPlayer.hasNextNode()) {
                gameBoard.nextMove();
            } else break;
        }
    }

    /**
     * Register turns to the players
     *
     * @param player The player to register turns
     * @param turns  The turns to add
     */
    private void addTurnsToPlayer(TestPlayer player, String... turns) {
        for (String turn : turns) {
            player.registerNodeToPlay(new GameNode(gameBoard, null, GameNode.Action.PUT_STONE, Intersection.get(turn, gameBoard), player));
        }
    }

    /**
     * Test player to simulate players on GameBoard.
     * This are simple players where you can register all turns to act.
     */
    private class TestPlayer extends Player {

        public LinkedList<GameNode> nodesToPlay = new LinkedList<GameNode>();

        public TestPlayer() {
            listener = new TestPlayerTurnListener(this);
        }

        public void registerNodeToPlay(GameNode node) {
            nodesToPlay.add(node);
        }

        public GameNode getNextNode() {
            return nodesToPlay.pop();
        }

        public boolean hasNextNode() {
            return 0 != nodesToPlay.size();
        }

        @Override
        public String getDisplayName() {
            return "TestPlayer" + hashCode();
        }
    }

    private class TestPlayerTurnListener extends AbstractTurnListener {

        private final TestPlayer player;

        public TestPlayerTurnListener(TestPlayer player) {
            this.player = player;
        }

        @Override
        public void newTurn(GameBoard board, Player playerFromBoard) {
            if (player.hasNextNode()) {
                board.play(player.getNextNode());
            } else {
                throw new IllegalStateException("Has no more turn to play ...");
            }
        }
    }

    /**
     * IA Test player.
     */
    private abstract class IATestPlayer extends TestPlayer implements IAPlayer{}
}
