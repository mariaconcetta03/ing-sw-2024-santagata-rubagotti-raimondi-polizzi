package controller;

import Exceptions.CardNotOwnedException;
import junit.framework.TestCase;
import org.model.Game;
import org.model.ObjectiveCard;
import org.model.ObjectiveDeck;
import org.model.Player;

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

    public void testDrawCard() {
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