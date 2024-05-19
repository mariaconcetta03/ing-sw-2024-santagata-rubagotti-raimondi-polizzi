package controller;

import CODEX.controller.GameController;
import CODEX.controller.ServerController;
import CODEX.Exceptions.FullLobbyException;
import CODEX.Exceptions.GameAlreadyStartedException;
import CODEX.Exceptions.GameNotExistsException;
import CODEX.Exceptions.NicknameAlreadyTakenException;
import junit.framework.TestCase;
import CODEX.org.model.Player;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerControllerTest extends TestCase {

    public void testStartLobby() throws RemoteException {
        ServerController s1=new ServerController();
        Player p1= new Player();
        p1.setNickname("Pippo");
        s1.startLobby(p1.getNickname(),4);
        s1.startLobby("DoubleDuck", 3);
        s1.startLobby("Paperinik", 2);
        assertThrows(IllegalArgumentException.class, ()->{s1.startLobby("Orango", 1);});
        assertThrows(IllegalArgumentException.class, ()->{s1.startLobby("MacchiaNera", -50);});
        assertThrows(IllegalArgumentException.class, ()->{s1.startLobby("Ciccio", 7);});
        for(GameController gc: s1.getAllGameControllers().values()){
            System.out.println("Available games are the ones with id: "+gc.getId());
        }
    }
    //Bisogna impedire ad un giocatore gia in una partita di entrare in un'altra?
    public void testAddPlayerToLobby() throws RemoteException {
        ServerController s1 = new ServerController();
        Player p1 = new Player();
        p1.setNickname("Pippo");
        Player p2 = new Player();
        p2.setNickname("Pluto");
        Player p3 = new Player();
        p3.setNickname("Paperino");
        Player p4 = new Player();
        p4.setNickname("Topolino");
        Player p5 = new Player();
        p5.setNickname("Minnie");
        Player p6 = new Player();
        p6.setNickname("Paperoga");
        s1.startLobby(p1.getNickname(), 4);
        s1.startLobby(p2.getNickname(), 3);
        System.out.print("Game you can join: ");
        for (GameController gc : s1.getAllGameControllers().values()) {
            System.out.print(gc.getId()+" ");
        }
        try {
            s1.addPlayerToLobby(p3.getNickname(), 0);
            s1.addPlayerToLobby(p4.getNickname(), 0);
            s1.addPlayerToLobby(p5.getNickname(), 0);
            s1.addPlayerToLobby(p6.getNickname(), 1);
        }catch (GameNotExistsException | GameAlreadyStartedException | FullLobbyException ignored){}
        assertThrows(GameAlreadyStartedException.class, ()->{s1.addPlayerToLobby(p6.getNickname(), 0);}); //when we launch all the test together it might produce GameNotExistsException
        assertThrows(GameNotExistsException.class, ()->{s1.addPlayerToLobby(p6.getNickname(), 2);});


    }

    public void testChooseNickname() throws RemoteException {
        ServerController s1= new ServerController();
        try {
            s1.chooseNickname("Pluto");
        }catch (NicknameAlreadyTakenException ignored){}
        assertEquals("Pluto", s1.getAllNicknames().get(0));
        s1.getAllNicknames().add("Pippo");
        assertThrows(NicknameAlreadyTakenException.class, ()->{s1.chooseNickname("Pippo");});
    }

    public void testIsNicknameAvailable() {
        Player p1=new Player();
        p1.setNickname("Pippo");
        Player p2= new Player();
        p2.setNickname("Pluto");
        Player p3= new Player();
        p3.setNickname("Paperino");
        ServerController s1= new ServerController();
        s1.getAllNicknames().add(p1.getNickname());
        s1.getAllNicknames().add(p2.getNickname());
        s1.getAllNicknames().add(p3.getNickname());
        assertFalse(s1.isNicknameAvailable("Pippo"));
        assertTrue(s1.isNicknameAvailable("Cesare"));
    }
}