package controller;

import Exceptions.CardNotOwnedException;
import junit.framework.TestCase;
import org.model.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameControllerTest extends TestCase {

    public void testCreateGame() {
        GameController g1= new GameController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);
        g1.createGame(players);
        assertEquals(g1.getGame().getId(), ServerController.getFirstAvailableId()-1);
        assertEquals(g1.getGame().getState(), Game.GameState.WAITING_FOR_START);
    }

    public void testAddPlayer() {

    }

    public void testStartGame() {
        GameController g1= new GameController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");
        List<Player> players=new ArrayList<>();
        players.add(p1);
        g1.setNumberOfPlayers(4);
        players.add(p2);
        players.add(p3);
        players.add(p4);
        g1.createGame(players);
        g1.startGame();
        System.out.println("Ciao");
    }

    public void testPlayCard() {

    }

    //problemi in player, non vengono tolte le carte dai deck quando chiamo drawCard().
    //2_ Chi controlla se sto pescando una carta legittima allora?
    //3_ Passiamo i player per nickname o Player? Passiamo le carte per id o meno?
    public void testDrawCard() {
        Scanner sc = new Scanner(System.in);
        GameController g1 = new GameController();
        ServerController s1= new ServerController();
        Player p1 = new Player();
        p1.setNickname("Pippo");
        Player p2 = new Player();
        p2.setNickname("Pluto");
        Player p3 = new Player();
        p3.setNickname("Paperino");
        Player p4 = new Player();
        p4.setNickname("Topolino");
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        s1.getAllPlayers().add(p4);
        g1.createGame(players);
        g1.startGame();
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());
        Player tmp= g1.getGame().getCurrentPlayer();
        PlayableCard[] cardsInHand= tmp.getPlayerDeck();
        cardsInHand[0]=null;
        System.out.println("Il giocatore corrente è: "+tmp.getNickname());
        g1.drawCard(tmp.getNickname(), g1.getGame().getGoldDeck().checkFirstCard());
        System.out.println("First card in hand: "+tmp.getPlayerDeck(1).getId());
        System.out.println("Second card in hand: "+tmp.getPlayerDeck(2).getId());
        System.out.println("Third card in hand: "+tmp.getPlayerDeck(3).getId());
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());
        cardsInHand[1]=null;
        System.out.println("Il giocatore corrente è: "+tmp.getNickname());
        g1.drawCard(tmp.getNickname(), g1.getGame().getResourceCard1());
        System.out.println("First card in hand: "+tmp.getPlayerDeck(1).getId());
        System.out.println("Second card in hand: "+tmp.getPlayerDeck(2).getId());
        System.out.println("Third card in hand: "+tmp.getPlayerDeck(3).getId());
        System.out.println("Common drawable GOLD card: " + g1.getGame().getGoldCard1().getId() + " " + g1.getGame().getGoldCard2().getId());
        System.out.println("Common drawable RESOURCE card: " + g1.getGame().getResourceCard1().getId() + " " + g1.getGame().getResourceCard2().getId());
        System.out.println("Top of the decks' card: " + g1.getGame().getGoldDeck().checkFirstCard().getId() + " " + g1.getGame().getResourceDeck().checkFirstCard().getId());

    }

    public void testChooseObjectiveCard() {
        ObjectiveDeck obDeck= ObjectiveDeck.objectiveDeck();
        GameController g1= new GameController();
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2=new Player();
        p2.setNickname("Pluto");
        Player p3=new Player();
        p3.setNickname("Paperino");
        Player p4= new Player();
        p4.setNickname("Topolino");
        List<Player> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);
        g1.createGame(players);
        g1.startGame();
        ObjectiveCard cardToBeSelected= p1.getPersonalObjective();
        g1.chooseObjectiveCard(p1, cardToBeSelected);
        assertEquals(cardToBeSelected,p1.getPersonalObjective());
    }

    public void testChoosePawnColor() {
    }

    public void testSendMessage() {
    }

    public void testNextPhase() {
    }

    public void testEndGame() {
    }

    public void testSetNumberOfPlayers() {
    }
}