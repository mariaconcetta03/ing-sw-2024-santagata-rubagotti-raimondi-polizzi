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
            System.out.println("Initial cards given");
        }
    }




    public void testWinner() {
        Deck deck = new Deck();
        ObjectiveCard[] obj = new ObjectiveCard[16];
        Player p1 = new Player();
        Game game = new Game(p1, 1, deck.resourceDeck(), deck.goldDeck(), deck.baseDeck(), obj);
        Player p = new Player(game);
        game.addPlayer(p);

        // adding points to the player
        p1.addPoints(5);
        p.addPoints(3);
        p1.setNickname("Pippo");
        p.setNickname("Pluto");



        // printing the points
        System.out.println("The player Pippo has 5 points");
        System.out.println("The player Pluto has 3 points");


        List <Player> winners = new ArrayList<>();
        winners = game.winner();

        for (int i = 0; i < winners.size(); i++) {
            System.out.println("The winner is/are: " + winners.get(i).getNickname());
        }

    }




    public void testStartChat() {
        Deck deck = new Deck();
        ObjectiveCard[] obj = new ObjectiveCard[16];
        Player p1 = new Player();
        Game game = new Game(p1, 1, deck.resourceDeck(), deck.goldDeck(), deck.baseDeck(), obj);
        Player p = new Player(game);
        game.addPlayer(p);
        System.out.println("Starting chat between P1 and P2");

        game.startChat(p, p1);

        for(int i = 0; i < game.getChats().size(); i++) {
            System.out.println("The number of the active chats is: " + game.getChats().size());
        }
    }




    public void testStartGeneralChat() {
        Deck deck = new Deck();
        ObjectiveCard[] obj = new ObjectiveCard[16];
        Player p1 = new Player();
        Game game = new Game(p1, 1, deck.resourceDeck(), deck.goldDeck(), deck.baseDeck(), obj);
        Player p = new Player(game);
        game.addPlayer(p);
        System.out.println("Starting general chat");

        game.startGeneralChat();
        for(int i = 0; i < game.getChats().size(); i++) {
            System.out.println("The number of the active chats is: " + game.getChats().size());
        }
    }




    public void testNextRound() {
        Deck deck = new Deck();
        ObjectiveCard[] obj = new ObjectiveCard[16];
        Player p1 = new Player();
        Game game = new Game(p1, 1, deck.resourceDeck(), deck.goldDeck(), deck.baseDeck(), obj);
        Player p = new Player(game);
        game.addPlayer(p);

        p1.setNickname("Pippo");
        p.setNickname("Pluto");

        System.out.println("The current playing order is FIRST PLAYER (" + game.getPlayers().get(0).getNickname() + "), " +
                "SECOND PLAYER (" + game.getPlayers().get(1).getNickname() + ")");

        // changing the playing order
        System.out.println("Changing the playing order...");
        game.nextRound();

        System.out.println("The new playing order is FIRST PLAYER (" + game.getPlayers().get(0).getNickname() + "), " +
                "SECOND PLAYER (" + game.getPlayers().get(1).getNickname() + ")");
    }




    public void testResetGoldCard1() {
        Deck deck = new Deck();
        ObjectiveCard[] obj = new ObjectiveCard[16];
        Player p1 = new Player();
        Game game = new Game(p1, 1, deck.resourceDeck(), deck.goldDeck(), deck.baseDeck(), obj);

        System.out.println("The previous gold card (1) had the ID: " + game.getGoldCard1().getId());
        game.resetGoldCard1();
        System.out.println("the new gold card (1) has the ID " +  game.getGoldCard1().getId());
    }



    public void testResetGoldCard2() {
        Deck deck = new Deck();
        ObjectiveCard[] obj = new ObjectiveCard[16];
        Player p1 = new Player();
        Game game = new Game(p1, 1, deck.resourceDeck(), deck.goldDeck(), deck.baseDeck(), obj);

        System.out.println("The previous gold card (2) had the ID: " + game.getGoldCard2().getId());
        game.resetGoldCard2();
        System.out.println("the new gold card (2) has the ID " +  game.getGoldCard2().getId());
    }



    public void testResetResourceCard1() {
        Deck deck = new Deck();
        ObjectiveCard[] obj = new ObjectiveCard[16];
        Player p1 = new Player();
        Game game = new Game(p1, 1, deck.resourceDeck(), deck.goldDeck(), deck.baseDeck(), obj);

        System.out.println("The previous resource card (1) had the ID: " + game.getResourceCard1().getId());
        game.resetResourceCard1();
        System.out.println("the new resource card (1) has the ID " +  game.getResourceCard1().getId());
    }



    public void testResetResourceCard2() {
        Deck deck = new Deck();
        ObjectiveCard[] obj = new ObjectiveCard[16];
        Player p1 = new Player();
        Game game = new Game(p1, 1, deck.resourceDeck(), deck.goldDeck(), deck.baseDeck(), obj);

        System.out.println("The previous resource card (2) had the ID: " + game.getResourceCard2().getId());
        game.resetResourceCard2();
        System.out.println("the new resource card (2) has the ID " +  game.getResourceCard2().getId());
    }

}