package controller;

import junit.framework.TestCase;
import org.model.Player;

public class ServerControllerTest extends TestCase {

    public void testStartLobby() {
    }

    public void testAddPlayerToLobby() {
    }

    public void testChooseNickname() {
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