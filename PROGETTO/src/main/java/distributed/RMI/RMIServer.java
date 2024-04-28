package distributed.RMI;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import controller.GameController;
import controller.ServerController;
import org.model.*;


import java.rmi.NotBoundException;
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
        System.out.println("Server ready"); // OK
    }




    /**
     * This method calls the function into the ServerController
     * @param creator is the player who wants to create a new lobby
     * @param numOfPlayers is the number of player the creator decided can play in the lobby
     * @throws RemoteException
     */
    public void createLobby (Player creator, int numOfPlayers) throws RemoteException {
        serverController.startLobby(creator, numOfPlayers);
    }




    /**
     * This method calls the function into the ServerController
     * @param player is the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
     * @throws RemoteException
     * @throws GameAlreadyStartedException
     * @throws FullLobbyException
     * @throws GameNotExistsException
     */
    public void addPlayerToLobby (Player player, int gameId) throws RemoteException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException {
        serverController.addPlayerToLobby(player, gameId);
    }




    /**
     * This method calls the function into the ServerController
     * @param chooser is the player choosing the nickname
     * @param nickname is the String he wants to put as his nickname
     * @throws RemoteException
     * @throws NicknameAlreadyTakenException
     */
    public void chooseNickname (Player chooser, String nickname) throws RemoteException, NicknameAlreadyTakenException {
        serverController.chooseNickname(chooser, nickname);
    }




    /**
     * This method calls the function into the gameController
     * @param gamePlayers is the List of players that will be in the Game
     * @throws RemoteException
     */
    public void createGame (List<Player> gamePlayers) throws RemoteException {
       gameController.createGame(gamePlayers);
    }




    /**
     * This method calls the function into the gameController
     * @param player is the player who wants to be added
     * @throws RemoteException
     * @throws ArrayIndexOutOfBoundsException
     */
    public void addPlayerToGame (Player player) throws RemoteException, ArrayIndexOutOfBoundsException {
        gameController.addPlayer(player);
    }




    /**
     * This method calls the function into the gameController
     * @throws RemoteException
     * @throws IllegalArgumentException
     */
    public void startGame() throws RemoteException, IllegalStateException {
        gameController.startGame();
    }




    /**
     * This method calls the function into the gameController
     * @param nickname is the nickname of the Player that wants to play a card
     * @param baseCard is the base card that is played
     * @param orientation the side on which the Player wants to play the Card
     * @throws RemoteException
     */
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException {
        gameController.playBaseCard(nickname, baseCard, orientation);
    }





    /**
     * This method calls the function into the gameController
     * @param selectedCard the Card the Player wants to play
     * @param position the position where the Player wants to play the Card
     * @param orientation the side on which the Player wants to play the Card
     * @throws RemoteException
     */
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException {
        gameController.playCard(nickname, selectedCard, position, orientation);
    }




    /**
     * This method calls the function into the gameController
     * @param nickname is the nickname of the player who wants to draw the card
     * @param selectedCard is the Card the Players wants to draw
     * @throws RemoteException
     */
    public void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException {
        gameController.drawCard(nickname, selectedCard);
    }




    /**
     * This method calls the function into the gameController
     * @param chooser is the player selecting the ObjectiveCard
     * @param selectedCard is the ObjectiveCard the player selected
     * @throws RemoteException
     */
    public void chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard) throws RemoteException {
        gameController.chooseObjectiveCard(chooser, selectedCard);
    }




    /**
     * This method allows a player to choose the color of his pawn
     * @param chooser is the player who needs to choose the color
     * @param selectedColor is the color chosen by the player
     * @throws RemoteException
     */
    public void choosePawnColor(Player chooser, Pawn selectedColor) throws RemoteException {
        gameController.choosePawnColor(chooser, selectedColor);
    }




    /**
     * This method calls the function into the gameController
     * @param sender is the player sending the message
     * @param receivers is the list of the players who need to receive this message
     * @param message is the string sent by the sender to the receivers
     * @throws RemoteException
     */
   public void sendMessage(Player sender, List<Player> receivers, String message) throws RemoteException {
       gameController.sendMessage(sender, receivers, message);
   }




    /**
     * This method calls the function into the gameController
     * @throws RemoteException
     */
    public void nextRound() throws RemoteException {
        gameController.nextPhase();
    }




    /**
     * This method calls the function into the gameController
     * @throws RemoteException
     */
    public void endGame() throws RemoteException {
        gameController.endGame();
    }




    /**
     * This method calls the function into the gameController
     * @param nickname of the player
     * @throws RemoteException
     * @throws IllegalArgumentException
     */
    public void leaveGame(String nickname) throws RemoteException, IllegalArgumentException {
        gameController.leaveGame(nickname);
    }




    /**
     * Settings class
     * It is about port and ip address of the server
     */
    public static class Settings { //this is an attribute
        static int PORT = 1099; // free ports: from 49152 to 65535, 1099 default port for RMI registry
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)
    }




    /**
     * Setter method
     * @param caller is player with a specific associated game, who calls the methods
     * @throws RemoteException
     */
    public void setGameController(Player caller) throws RemoteException {
        this.gameController =
                ServerController.getAllGameControllers().get(caller.getGame().getId());
    }

}
