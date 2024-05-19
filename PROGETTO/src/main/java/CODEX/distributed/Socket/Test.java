package CODEX.distributed.Socket;

import java.util.Timer;

public class Test {
        public static void main(String[] args) {
            try {
                Timer t=new Timer();
                ClientSCK a = new ClientSCK();
                ClientSCK b = new ClientSCK();
                ClientSCK c = new ClientSCK();
                ClientSCK d = new ClientSCK();
                a.createLobby("a",4);
                b.addPlayerToLobby("b",a.gameID);
                c.addPlayerToLobby("b",a.gameID);
                d.addPlayerToLobby("b",a.gameID);


            } catch (Exception e) {
                System.err.println();
            }
        }
    }

