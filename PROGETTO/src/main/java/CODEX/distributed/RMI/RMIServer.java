package CODEX.distributed.RMI;

import CODEX.Exceptions.FullLobbyException;
import CODEX.Exceptions.GameAlreadyStartedException;
import CODEX.Exceptions.GameNotExistsException;
import CODEX.Exceptions.NicknameAlreadyTakenException;
import CODEX.controller.GameController;
import CODEX.controller.ServerController;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Set;


public class RMIServer extends UnicastRemoteObject implements ServerRMIInterface {
    // il server è unico e al suo interno ha un attributo Game Controller che mi
    // permette di dare il turno ai giocatori di un Game (senza unire giocatori di
    // più Game)

    private ServerController serverController;


    /**
     * Class constructor
     * @param serverController
     * @throws RemoteException
     */
    public RMIServer(ServerController serverController) throws RemoteException {
        this.serverController = serverController;
    }




    /**
     * Main method
     * This method calls the method "startServer". After the calling of this method the server
     * is able to receive the requests of the clients
     * @param args from CLI
     * @throws RemoteException
     */
    public static void main (String[] args) throws RemoteException {
        try {
            new RMIServer(new ServerController()).startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * This method starts the server, so it can listen to the clients and receive
     * their requests (the clients will invoke functions on the server)
     * @throws RemoteException
     */
    @Override
    public void startServer() throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(Settings.PORT); // putting the server into the registry
        try {
            registry.bind("Server", this); // setting the name of the server (remote obj/interface)
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("RMI Server ready"); // OK
    }




    /**
     * This method calls the function into the ServerController
     * @param creatorNickname is the nickname of the player who wants to create a new lobby
     * @param numOfPlayers is the number of player the creator decided can play in the lobby
     * @throws RemoteException
     * @return serverController.addPlayerToLobby(player, gameId) is the game controller of the match
     */
    public GameControllerInterface createLobby (String creatorNickname, int numOfPlayers) throws RemoteException {
        return serverController.startLobby(creatorNickname, numOfPlayers);
    }




    /**
     * This method calls the function into the ServerController
     * @param playerNickname is the nickname of the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
     * @throws RemoteException
     * @throws GameAlreadyStartedException
     * @throws FullLobbyException
     * @throws GameNotExistsException
     * @return serverController.addPlayerToLobby(player, gameId) is the game controller of the match
     */
    public GameControllerInterface addPlayerToLobby (String playerNickname, int gameId) throws RemoteException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException {
        return serverController.addPlayerToLobby(playerNickname, gameId);
    }




    /**
     * This method calls the function into the ServerController
     * @param nickname is the String he wants to put as his nickname
     * @throws RemoteException
     * @throws NicknameAlreadyTakenException
     */
    public void chooseNickname (String nickname) throws RemoteException, NicknameAlreadyTakenException {
        serverController.chooseNickname(nickname);
    }

    @Override
    public Map<Integer, GameController> getAllGameControllers() throws RemoteException {
        return serverController.getAllGameControllers();
    }

    @Override
    public Set<Integer> getAvailableGameControllersId() throws RemoteException {
        return serverController.getAvailableGameControllersId();
    }

    /**
    @Override
    public void addLobbyClient(RMIClient client) throws RemoteException {
        WrappedObserver wrapObs= new WrappedObserver(client);
        serverController.addLobbyClient((Observer) wrapObs);
    }
    */

    /**
     * Settings class
     * It is about port and ip address of the server
     */
    public static class Settings { //this is an attribute
        static int PORT = 1099; // free ports: from 49152 to 65535, 1099 default port for RMI registry
        static String SERVER_NAME = "172.20.10.8"; // LOCALHOST (every client has the same virtual server at this @address)
    }
}
