package org.model;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class GameTest extends TestCase {

    public void testAddPlayer() {
        Deck deck = new Deck();
        ObjectiveCard[] obj = new ObjectiveCard[16];
        Player p1 = new Player();
        Game game = new Game(p1, 1, deck.resourceDeck(), deck.goldDeck(), deck.baseDeck(), obj);
        Player p = new Player(game);
        List<Player> players = new ArrayList<>();
        players = game.getPlayers();
        game.addPlayer(p);

        for (int i = 0; i< players.size(); i++) {
            System.out.println("Player number" + i + "with nickname" + players.get(i).getNickname());
        }
    }




    public void testStartGame() {
        Deck deck = new Deck();
        ObjectiveCard[] obj = new ObjectiveCard[16];
        Player p1 = new Player();
        Game game = new Game(p1, 1, deck.resourceDeck(), deck.goldDeck(), deck.baseDeck(), obj);
        Player p = new Player(game);
        game.addPlayer(p);

        game.setnPlayers(4);
        int numPlayers = game.getnPlayers();

        List<Integer> usedIndexes = new ArrayList<>();
        usedIndexes = game.startGame();

        // printing the game status
        if (game.getState().equals(Game.GameState.STARTED)) {
            System.out.println("The game is set to STARTED!");
        }

        // printing the colours of the pawns
        for (int i = 0; i<numPlayers; i++) {
            System.out.println ("The player number " + i+1 + " has chosen the Pawn: " + game.getPlayers().get(i).getChosenColor());
        }

        // printing the cards on the market
        System.out.println("The 1 card on market has ID: " + game.getResourceCard1().getId());
        System.out.println("The 2 card on market has ID: " + game.getResourceCard2().getId());
        System.out.println("The 3 card on market has ID: " + game.getGoldCard1().getId());
        System.out.println("The 4 card on market has ID: " + game.getGoldCard2().getId());

        // printing the common objectives
        System.out.println("The 1st common objective has ID: " + game.getObjectiveCard1().getId());
        System.out.println("The 2nd common objective has ID: " + game.getObjectiveCard2().getId());

        // printing the used indexes for the Objective deck

    }

    public void testGiveInitialCards() {
        Deck deck = new Deck();
        ObjectiveCard[] obj = new ObjectiveCard[16];
        Player p1 = new Player();
        Game game = new Game(p1, 1, deck.resourceDeck(), deck.goldDeck(), deck.baseDeck(), obj);
        int nPlayers = game.getnPlayers();
        List<Player> players = new ArrayList<>();
        players = game.getPlayers();

        game.giveInitialCards();

        for (int i=0; i<nPlayers; i++) {
            System.out.println("Initial card of " + i + "player is " + players.get(i).drawCard(deck.resourceDeck.getFirstCard().getId()); gggg);
        }
    }




    public void testWinner() {
    }

    public void testStartChat() {
    }

    public void testStartGeneralChat() {
    }

    public void testNextRound() {
    }

    public void testResetGoldCard1() {
    }

    public void testResetGoldCard2() {
    }

    public void testResetResourceCard1() {
    }

    public void testResetResourceCard2() {
    }
}