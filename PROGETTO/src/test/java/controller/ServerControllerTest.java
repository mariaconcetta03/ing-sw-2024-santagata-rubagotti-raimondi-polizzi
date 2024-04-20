package controller;

import Exceptions.NicknameAlreadyTakenException;
import junit.framework.TestCase;
import org.model.Player;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerControllerTest extends TestCase {

    public void testStartLobby() {
        ServerController s1=new ServerController();
        Player p1= new Player();
        p1.setNickname("Pippo");
        s1.startLobby(p1,4);
        //not finished
    }

    public void testAddPlayerToLobby() {
    }

    public void testChooseNickname() {
        Player p1=new Player();
        ServerController s1= new ServerController();
        s1.getAllPlayers().add(p1);
        try {
            s1.chooseNickname(p1, "Pluto");
        }catch(NicknameAlreadyTakenException ignored){}
        assertEquals("Pluto", p1.getNickname());
        Player p2=new Player();
        p2.setNickname("Pippo");
        s1.getAllPlayers().add(p2);
        Player p3=new Player();
        s1.getAllPlayers().add(p3);
        assertThrows(NicknameAlreadyTakenException.class, ()->{s1.chooseNickname(p3, "Pippo");});
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