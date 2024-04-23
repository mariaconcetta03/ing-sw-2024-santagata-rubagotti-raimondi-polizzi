package controller;

import Exceptions.NicknameAlreadyTakenException;
import junit.framework.TestCase;
import org.model.Player;
import utils.Event;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerControllerTest extends TestCase {

    public void testStartLobby() {
        ServerController s1=new ServerController();
        Player p1= new Player();
        p1.setNickname("Pippo");
        s1.startLobby(p1,4);
        s1.startLobby(new Player(), 3);
        s1.startLobby(new Player(), 2);
        assertEquals(s1.startLobby(new Player(), 1), Event.WRONG_NUMBER_OF_PLAYERS);
        assertEquals(s1.startLobby(new Player(), -50), Event.WRONG_NUMBER_OF_PLAYERS);
        assertEquals(s1.startLobby(new Player(), 7), Event.WRONG_NUMBER_OF_PLAYERS);
        for(GameController gc: s1.getAllGameControllers().values()){
            System.out.println("Partite create sono quelle con id: "+gc.getId());
        }
    }
    //Bisogna impedire ad un giocatore gia in una partita di entrare in un'altra?
    public void testAddPlayerToLobby() {
        ServerController s1 = new ServerController();
        Player p1 = new Player();
        p1.setNickname("Pippo");
        Player p2 = new Player();
        p1.setNickname("Pluto");
        Player p3 = new Player();
        p1.setNickname("Paperino");
        Player p4 = new Player();
        p1.setNickname("Topolino");
        Player p5 = new Player();
        p1.setNickname("Minnie");
        Player p6 = new Player();
        p1.setNickname("Paperoga");
        s1.startLobby(p1, 4);
        s1.startLobby(p2, 3);
        System.out.print("Game you can join: ");
        for (GameController gc : s1.getAllGameControllers().values()) {
            System.out.print(gc.getId()+" ");
        }
        s1.addPlayerToLobby(p3, 0);
        s1.addPlayerToLobby(p4, 0);
        s1.addPlayerToLobby(p5, 0);
        s1.addPlayerToLobby(p6, 0);
        s1.addPlayerToLobby(p6, 2);
        s1.addPlayerToLobby(p6, 1);
    }

    public void testChooseNickname() {
        Player p1=new Player();
        ServerController s1= new ServerController();
        s1.getAllPlayers().add(p1);
        s1.chooseNickname(p1, "Pluto");
        assertEquals("Pluto", p1.getNickname());
        Player p2=new Player();
        p2.setNickname("Pippo");
        s1.getAllPlayers().add(p2);
        Player p3=new Player();
        s1.getAllPlayers().add(p3);
        assertEquals(Event.NICKNAME_ALREADY_TAKEN, s1.chooseNickname(p3, "Pippo"));
    }

    public void testIsNicknameAvailable() {
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2= new Player();
        p2.setNickname("Pluto");
        Player p3= new Player();
        p3.setNickname("Paperino");
        ServerController s1= new ServerController();
        s1.getAllPlayers().add(p1);
        s1.getAllPlayers().add(p2);
        s1.getAllPlayers().add(p3);
        assertFalse(s1.isNicknameAvailable("Pippo"));
        assertTrue(s1.isNicknameAvailable("Cesare"));
    }
}