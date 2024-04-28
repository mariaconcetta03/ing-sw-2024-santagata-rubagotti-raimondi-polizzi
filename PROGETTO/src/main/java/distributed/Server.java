package distributed;

import java.rmi.RemoteException;
import java.rmi.server.*;
import java.util.List;

import org.model.*;


/**
 * SINGLETON CLASS
 * This is the only one server
 */
public class Server extends UnicastRemoteObject {

    private static Server instance;

    public static Server getInstance() throws RemoteException {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    Server() throws RemoteException {
    }

    public static void updateBoard(Board board, Game game) {
        // gliela piazzo nella chiamata di Server --> serverRMI --> clientRMI --> view--> controller --> model
    }

    public static void updateResourceDeck(){

    }
    public static void updateGoldDeck(){

    }
    public static void updatePlayerDeck(Player player){

    }
    public static void updateResourceCard1(){

    }
    public static void updateResourceCard2(){

    }
    public static void updateGoldCard2(){

    }
    public static void updateGoldCard1(){

    }
    public static void updateChat(int chatID){

    }
    public static void updatePawns(){

    }
    public static void updateNickname(){

    }
    public static void updateRound(){

    }



}
