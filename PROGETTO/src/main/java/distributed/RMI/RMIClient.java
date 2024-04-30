package distributed.RMI;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import distributed.ClientGeneralInterface;
import org.model.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;


// ----------------------------------- H O W   I T   W O R K S ----------------------------------------
// RMI CLIENT CALLS THE REMOTE METHODS THROUGH THE RMISERVER INTERFACE (WHICH RMI CLIENT HAS INSIDE)
// THE RMI SERVER GOES TO MODIFY THE MODEL (UPON CLIENT REQUEST) THROUGH THE CONTROLLER.
// TO UNDERSTAND WHETHER AN EVENT WAS SUCCESSFUL OR NOT, YOU NEED TO CONSULT THE ATTRIBUTE
// "lastEvent" PRESENT IN THE GAME CLASS.
// ----------------------------------------------------------------------------------------------------


// ---------------------------------- #  T O   D O ---------------------------------------
// RIVEDERE I METODI, NON TUTTI POSSONO ESSERE INVOCATI DAL CLIENT, ALCUNI DI QUESTI
// VERRANNO INVOCATI AUTOMATICAMENTE DAL CONTROLLER, DI CONSEGUENZA DEVONO ESSERE RIMOSSI
// ---------------------------------------------------------------------------------------


public class RMIClient extends UnicastRemoteObject implements ClientGeneralInterface {
    private ServerRMIInterface SRMIInterface; //following the slides' instructions


    /**
     * Class constructor
     * @throws RemoteException
     */
    protected RMIClient() throws RemoteException {
    }




    /**
     * Main method
     * This method calls the method "startConnectionWithServer()". After the calling of this method the server
     * is able to receive the requests of the clients
     * @param args from CLI
     */
    public static void main( String[] args )
    {
        try {
            new RMIClient().createLobby(new Player(), 4); // OK
            new RMIClient().createLobby(new Player(), 9); // KO [java.lang.IllegalArgumentException: Wrong number of players!]
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    /**
     * This method gets the SRMIInterface from the registryServer
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void SRMIInterfaceFromRegistry() throws RemoteException, NotBoundException {
        Registry registryServer = null;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT); // getting the registry
        // Looking up the registry for the remote object
        this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
    }




    /**
     * This method calls the function into the ServerController
     * @param creator is the player who wants to create a new lobby
     * @param numOfPlayers is the number of player the creator decided can play in the lobby
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void createLobby(Player creator, int numOfPlayers) throws RemoteException, NotBoundException { //exceptions added automatically
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.createLobby(creator, numOfPlayers);
    }




    /**
     * This method is used to add a single player to an already created lobby
     * @param player is the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
     * @throws RemoteException
     * @throws NotBoundException
     * @throws GameAlreadyStartedException
     * @throws FullLobbyException
     * @throws GameNotExistsException
     */
    public void addPlayerToLobby (Player player, int gameId) throws RemoteException, NotBoundException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.addPlayerToLobby(player, gameId);
    }




    /**
     * Once connected the player get to choose his nickname that must be different from all the other presents
     * @param chooser is the player choosing the nickname
     * @param nickname is the String he wants to put as his nickname
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void chooseNickname (Player chooser, String nickname) throws RemoteException, NotBoundException, NicknameAlreadyTakenException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.chooseNickname(chooser, nickname);
    }




    /**
     * This method creates the Game that will be managed by GameController
     * @param gamePlayers is the List of players that will be in the Game
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void createGame (List<Player> gamePlayers) throws RemoteException, NotBoundException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.createGame(gamePlayers);
    }




    /**
     * This method adds the player to a game
     * @param player is the player who wants to be added
     * @throws RemoteException
     * @throws NotBoundException
     * @throws ArrayIndexOutOfBoundsException
     */
    public void addPlayerToGame (Player player) throws RemoteException, NotBoundException, ArrayIndexOutOfBoundsException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.addPlayerToGame(player);
    }




    /**
     * This method starts the game. The game state is set to STARTED. 2 objective cards are given to each
     * player, and he will need to choose one of these. Then the market is completed and each player receives
     * 3 cards (2 resource cards and 1 gold card)
     * @throws RemoteException
     * @throws NotBoundException
     * @throws IllegalStateException
     */
    public void startGame() throws RemoteException, NotBoundException, IllegalStateException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.startGame();
    }




    /**
     * This method "plays" the card selected by the Player in his own Board
     * @param selectedCard the Card the Player wants to play
     * @param position the position where the Player wants to play the Card
     * @param orientation the side on which the Player wants to play the Card
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, NotBoundException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.playCard(nickname, selectedCard, position, orientation);
    }




    /**
     * This method let the Player place the baseCard (in an already decided position) and, if all the players
     * have placed their baseCard, it let the game finish the set-up phase giving the last necessary cards
     * @param nickname is the nickname of the Player that wants to play a card
     * @param baseCard is the base card that is played
     * @param orientation the side on which the Player wants to play the Card
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException, NotBoundException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.playBaseCard(nickname, baseCard, orientation);
    }




    /**
     * This method allows the currentPlayer to draw a card from the decks or from the unveiled ones
     * @param nickname is the nickname of the player who wants to draw the card
     * @param selectedCard is the Card the Players wants to draw
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException, NotBoundException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.drawCard(nickname, selectedCard);
    }




    /**
     * This method allows the chooser Player to select his personal ObjectiveCard
     * @param chooser is the player selecting the ObjectiveCard
     * @param selectedCard is the ObjectiveCard the player selected
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard) throws RemoteException, NotBoundException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.chooseObjectiveCard(chooser, selectedCard);
    }




    /**
     * This method allows a player to choose the color of his pawn
     * @param chooser is the player who needs to choose the color
     * @param selectedColor is the color chosen by the player
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void choosePawnColor(Player chooser, Pawn selectedColor) throws RemoteException, NotBoundException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.choosePawnColor(chooser, selectedColor);
    }







    /**
     * This method sends a message from the sender to the receiver
     * @param sender is the player sending the message
     * @param receivers is the list of the players who need to receive this message
     * @param message is the string sent by the sender to the receivers
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void sendMessage(Player sender, List<Player> receivers, String message) throws RemoteException, NotBoundException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.sendMessage(sender, receivers, message);
    }




    /**
     * This method invokes a method in game, which does the necessary actions for the next round.
     * If the game state is ENDING, then the last rounds are done. After that, endGame is invoked.
     * we have decided that is the controller the one that manages the changing of turn
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void nextRound() throws RemoteException, NotBoundException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.nextRound();
    }




    /**
     * This method ends the game. It sets the game state to ENDED, checks all the objectives (2 common objs
     * and 1 personalObj) and adds the points to the correct player.
     * Finally, it checks the winner (or winners) of the game, and puts them in a list called "winners"
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void endGame() throws RemoteException, NotBoundException {
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.endGame();
    }




    /**
     * This method lets a player end the game (volontary action or involontary action - connection loss)
     * @param nickname of the player who is gonna leave the game
     * @throws RemoteException
     * @throws NotBoundException
     * @throws IllegalArgumentException
     */
    public void leaveGame(String nickname) throws RemoteException, NotBoundException, IllegalArgumentException{
        SRMIInterfaceFromRegistry();
        this.SRMIInterface.leaveGame(nickname);
    }




    /**
     * Settings class
     * It is about port and ip address of the server which the client needs to communicate with
     */
    public static class Settings { //this is an attribute
        static int PORT = 1099; // free ports: from 49152 to 65535, 1099 standard port for RMI registry
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)
    }

}
