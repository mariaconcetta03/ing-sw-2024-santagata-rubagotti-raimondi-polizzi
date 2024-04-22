package distributed.RMI;

import controller.GameController;
import controller.ServerController;
import distributed.ClientGeneralInterface;
import org.model.*;
import utils.*;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;



public class RMIServer extends UnicastRemoteObject implements ServerRMIInterface {
    // il server è unico e al suo interno ha un attributo Game Controller che mi
    // permette di dare il turno ai giocatori di un Game (senza unire giocatori di
    // più Game)


    ServerController serverController;
    GameController gameController;



    /**
     * Class constructor
     * @param serverController
     * @throws RemoteException
     */
    RMIServer(ServerController serverController) throws RemoteException {
        this.serverController = serverController;
        this.gameController = null;
    }




    /**
     * Main method
     * This method calls the method "startServer". After the calling of this method the server
     * is able to receive the requests of the clients
     * @param args none
     * @throws RemoteException
     */
    public static void main (String[] args) throws RemoteException {
        try {
            new RMIServer().startServer();
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
        Registry registry = LocateRegistry.createRegistry(Settings.PORT);
        try {
            registry.bind("Server", this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Server ready");
    }




    /**
     * This method calls the function into the ServerController
     * @param creator is the player who wants to create a new lobby
     * @param numOfPlayers is the number of player the creator decided can play in the lobby
     * @return the event "OK" when the lobby has been created on the server
     * @throws RemoteException
     */
    public Event createLobby (Player creator, int numOfPlayers) throws RemoteException {
        return serverController.startLobby(creator, numOfPlayers);
    }




    /**
     * This method calls the function into the ServerController
     * @param player is the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
     * @return the event "OK" when the lobby has been created on the server
     * if the lobby is full it returns the event "FULL_LOBBY"
     * @throws RemoteException
     */
    public Event addPlayerToLobby (Player player, int gameId) throws RemoteException {
        return serverController.addPlayerToLobby(player, gameId);
    }




    /**
     * This method calls the function into the ServerController
     * @param chooser is the player choosing the nickname
     * @param nickname is the String he wants to put as his nickname
     * @return the event "OK" if the nickname doesn't exist yet and so it is set,
     * if the nickname is already used by another player, it returns the event "NICKNAME_ALREADY_USED"
     */
    public Event chooseNickname (Player chooser, String nickname) throws RemoteException {
        return serverController.chooseNickname(chooser, nickname);
    }




    /**
     * This method calls the function into the gameController
     * @param gamePlayers is the List of players that will be in the Game
     * @return the event "OK" when the game has been created
     */
    public Event createGame (List<Player> gamePlayers) throws RemoteException {
       return gameController.createGame(gamePlayers);
    }




    /**
     * This method calls the function into the gameController
     * @param player is the player who wants to be added
     * @return the event "OK" when the player has been successfully added to the game,
     * instead it returns the event "FULL_LOBBY"
     */
    public Event addPlayerToGame (Player player) throws RemoteException {
        return gameController.addPlayer(player);
    }




    /**
     * This method calls the function into the gameController
     * @return the event "OK" when the game is set to started, otherwise,
     * if the game is not ready to be started, it returns the event "INVALID_GAME_STATUS"
     */
    public Event startGame() throws RemoteException {
        return gameController.startGame();
    }




    /**
     * This method calls the function into the gameController
     * @param selectedCard the Card the Player wants to play
     * @param position the position where the Player wants to play the Card
     * @param orientation the side on which the Player wants to play the Card
     * @return the event "OK" if the card has been successfully placed, otherwise it
     * returns the event "UNABLE_TO_PLAY_CARD"
     */
    public Event playCard(PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException {
      return gameController.playCard(selectedCard, position, orientation);
    }




    /**
     * This method calls the function into the gameController
     * @param nickname is the nickname of the player who wants to draw the card
     * @param selectedCard is the Card the Players wants to draw
     * @return the event "OK" when the player has drawn the card, otherwise, if
     * it's not his turn, it returns the event "NOT_YOUR_TURN"
     */
    public Event drawCard(String nickname, PlayableCard selectedCard) throws RemoteException {
        return gameController.drawCard(nickname, selectedCard);
    }




    /**
     * This method calls the function into the gameController
     * @param chooser is the player selecting the ObjectiveCard
     * @param selectedCard is the ObjectiveCard the player selected
     * @return the event "OK" if the card is correctly chosen, otherwise it returns
     * the event "OBJECTIVE_CARD_NOT_OWNED"
     */
    public Event chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard) throws RemoteException {
        return gameController.chooseObjectiveCard(chooser, selectedCard);
    }




    /**
     * This method calls the function into the gameController
     * @param sender is the player sending the message
     * @param receivers is the list of the players who need to receive this message
     * @param message is the string sent by the sender to the receivers
     * @return the event "OK" when the message has been sent successfully
     */
    public Event sendMessage(Player sender, List<Player> receivers, String message) throws RemoteException {
       return gameController.sendMessage(sender, receivers, message);
    }




    /**
     * This method calls the function into the gameController
     * @return the event "OK" when the current player has been changed
     */
    public Event nextRound() throws RemoteException {
       return gameController.nextPhase();
    }




    /**
     * This method calls the function into the gameController
     * @return the event "OK" when all the procedure about the enging of the game has
     * been successfully completed
     */
    public Event endGame() throws RemoteException {
        return gameController.endGame();
    }




    /**
     * Settings class
     * It is about port and ip address of the server with which the client communicates
     */
    public static class Settings { //this is an attribute
        static int PORT = 50001; // free ports: from 49152 to 65535
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)
    }




    /**
     * Setter method
     * @param serverController is the general Server Controller
     */
    public void setServerController(ServerController serverController){
        this.serverController = serverController;
    }




    /**
     * Setter method
     * @param gameController is the specific game controller
     */
    public void setGameController(GameController gameController) {
        // PRENDERE GAME CONTROLLER DAL SERVER CONTROLLER
    }

}
