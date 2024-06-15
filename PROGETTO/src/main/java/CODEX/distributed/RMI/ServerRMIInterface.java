package CODEX.distributed.RMI;

import CODEX.Exceptions.FullLobbyException;
import CODEX.Exceptions.GameAlreadyStartedException;
import CODEX.Exceptions.GameNotExistsException;
import CODEX.Exceptions.NicknameAlreadyTakenException;
import CODEX.controller.GameController;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;


/**
 * This interface represents the rmi server, which is unique and sets the round of different games
 */
public interface ServerRMIInterface extends Remote {

    /**
     * This method starts the server, so it can listen to the clients and receive
     * their requests (the clients will invoke functions on the server)
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void startServer() throws RemoteException;



    /**
     * This method calls the function into the ServerController
     * @param creatorNickname is the nickname of the player who wants to create a new lobby
     * @param numOfPlayers is the number of player the creator decided can play in the lobby
     * @return serverController.addPlayerToLobby(player, gameId) is the game controller of the match
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws IllegalArgumentException when the number of players is wrong
     */
    GameControllerInterface createLobby(String creatorNickname, int numOfPlayers) throws RemoteException, IllegalArgumentException;



    /**
     * This method calls the function into the ServerController
     * @param playerNickname is the nickname of the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
     * @return serverController.addPlayerToLobby(player, gameId) is the game controller of the match
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws GameAlreadyStartedException thrown if a game is already started
     * @throws FullLobbyException thrown if the lobby is full
     * @throws GameNotExistsException thrown if there's no game
     */
    GameControllerInterface addPlayerToLobby(String playerNickname, int gameId) throws RemoteException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException;



    /**
     * This method calls the function into the ServerController
     * @param nickname is the String he wants to put as his nickname
     * @throws RemoteException if an exception happens while communicating with the remote
     * @throws NicknameAlreadyTakenException if a nickname is already used by others players
     */
    void chooseNickname(String nickname) throws RemoteException, NicknameAlreadyTakenException;



    /**
     * Getter method
     * @return serverController.getAllGameControllers() which are the available game controllers
     * @throws RemoteException when there's a disconnection problem
     */
    Map<Integer, GameController> getAllGameControllers() throws RemoteException;



    /**
     * Getter method
     * @return serverController.getAvailableGameControllersId() which are the id of available game controllers
     * @throws RemoteException when there's a disconnection problem
     */
    Set<Integer> getAvailableGameControllersId() throws RemoteException;
}