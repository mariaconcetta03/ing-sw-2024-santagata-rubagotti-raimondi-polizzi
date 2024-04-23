package distributed.RMI;

import distributed.ClientGeneralInterface;
import org.model.*;
import utils.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;


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
     * @param args none
     */
    public static void main( String[] args )
    {
        try {
            new RMIClient().createLobby(new Player(), 4);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    /**
     * This method creates a new lobby
     * @throws RemoteException
     * @throws NotBoundException
     */
    public Event createLobby(Player creator, int numOfPlayers) throws RemoteException, NotBoundException { //exceptions added automatically
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT); // getting the registry
        // Looking up the registry for the remote object
        this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        event = this.SRMIInterface.createLobby(creator, numOfPlayers);
        event.printEvent();
        return event;
    }




    /**
     * This method is used to add a single player to an already created lobby
     * @param player is the player who wants to join the lobby
     * @param gameId is the lobby the player wants to join
     * @return the event "OK" when the lobby has been created on the server
     * if the lobby is full it returns the event "FULL_LOBBY". If the game doesn't
     * esists, it returns "GAME_NOT_EXISTS". If that game it's already started, it
     * returns "GAME_ALREADY_STARTED".
     * @throws RemoteException
     * @throws NotBoundException
     */
    public Event addPlayerToLobby (Player player, int gameId) throws RemoteException, NotBoundException {
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
            event = this.SRMIInterface.addPlayerToLobby(player, gameId);
        event.printEvent();
        return event;
    }




    /**
     * Once connected the player get to choose his nickname that must be different from all the other presents
     * @param chooser is the player choosing the nickname
     * @param nickname is the String he wants to put as his nickname
     * @return the event "OK" if the nickname doesn't exist yet and so it is set,
     * if the nickname is already used by another player, it returns the event "NICKNAME_ALREADY_USED"
     */
    public Event chooseNickname (Player chooser, String nickname) throws RemoteException, NotBoundException {
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        event = this.SRMIInterface.chooseNickname(chooser, nickname);
        event.printEvent();
        return event;
    }




    /**
     * This method creates the Game that will be managed by GameController
     * @param gamePlayers is the List of players that will be in the Game
     * @return the event "OK" when the game has been created
     */
    public Event createGame (List<Player> gamePlayers) throws RemoteException, NotBoundException {
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        event = this.SRMIInterface.createGame(gamePlayers);
        event.printEvent();
        return event;
    }




    /**
     * This method adds the player to a game
     * @param player is the player who wants to be added
     * @return the event "OK" when the player has been successfully added to the game,
     * instead it returns the event "FULL_LOBBY". If the game is already started, it returns
     * "GAME_ALREADY_STARTED"
     */
    public Event addPlayerToGame (Player player) throws RemoteException {
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        try {
            this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        event = this.SRMIInterface.addPlayerToGame(player);
        event.printEvent();
        return event;
    }




    /**
     * This method starts the game. The game state is set to STARTED. 2 objective cards are given to each
     * player, and he will need to choose one of these. Then the market is completed and each player receives
     * 3 cards (2 resource cards and 1 gold card)
     * @return the event "OK" when the game is set to started, otherwise,
     * if the game is not ready to be started, it returns the event "INVALID_GAME_STATUS"
     */
    public Event startGame() throws RemoteException {
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        try {
            this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        event = this.SRMIInterface.startGame();
        event.printEvent();
        return event;
    }




    /**
     * This method "plays" the card selected by the Player in his own Board
     * @param selectedCard the Card the Player wants to play
     * @param position the position where the Player wants to play the Card
     * @param orientation the side on which the Player wants to play the Card
     * @return the event "OK" if the card has been successfully placed, otherwise it
     * returns the event "UNABLE_TO_PLAY_CARD"
     */
    public Event playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException {
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        try {
            this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        event = this.SRMIInterface.playCard(nickname, selectedCard, position, orientation);
        event.printEvent();
        return event;
    }




    /**
     * This method let the Player place the baseCard (in an already decided position) and, if all the players
     * have placed their baseCard, it let the game finish the set-up phase giving the last necessary cards
     * @param nickname is the nickname of the Player that wants to play a card
     * @param baseCard is the base card that is played
     * @param orientation the side on which the Player wants to play the Card
     * @return the event "OK" after the base card has been placed
     * @throws RemoteException
     */
    public Event playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException {
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        try {
            this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        event = this.SRMIInterface.playBaseCard(nickname, baseCard, orientation);
        event.printEvent();
        return event;
    }




    /**
     * This method allows the currentPlayer to draw a card from the decks or from the unveiled ones
     * @param nickname is the nickname of the player who wants to draw the card
     * @param selectedCard is the Card the Players wants to draw
     * @return the event "OK" when the player has drawn the card, otherwise, if
     * it's not his turn, it returns the event "NOT_YOUR_TURN"
     */
    public Event drawCard(String nickname, PlayableCard selectedCard) throws RemoteException {
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        try {
            this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        event = this.SRMIInterface.drawCard(nickname, selectedCard);
        event.printEvent();
        return event;
    }




    /**
     * This method allows the chooser Player to select his personal ObjectiveCard
     * @param chooser is the player selecting the ObjectiveCard
     * @param selectedCard is the ObjectiveCard the player selected
     * @return the event "OK" if the card is correctly chosen, otherwise it returns
     * the event "OBJECTIVE_CARD_NOT_OWNED"
     */
    public Event chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard) throws RemoteException {
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        try {
            this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        event = this.SRMIInterface.chooseObjectiveCard(chooser, selectedCard);
        event.printEvent();
        return event;
    }




    /**
     * This method allows a player to choose the color of his pawn
     * @param chooser is the player who needs to choose the color
     * @param selectedColor is the color chosen by the player
     * @return the event "NOT_AVAILABLE_PAWN" if the color has been chosen by another player
     * returns the event "OK" if the color is chosen correctly
     * @throws RemoteException
     */
    public Event choosePawnColor(Player chooser, Pawn selectedColor) throws RemoteException{
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        try {
            this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        event = this.SRMIInterface.choosePawnColor(chooser, selectedColor);
        event.printEvent();
        return event;
    }







    /**
     * This method sends a message from the sender to the receiver
     * @param sender is the player sending the message
     * @param receivers is the list of the players who need to receive this message
     * @param message is the string sent by the sender to the receivers
     * @return the event "OK" when the message has been sent successfully
     */
    public Event sendMessage(Player sender, List<Player> receivers, String message) throws RemoteException {
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        try {
            this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        event = this.SRMIInterface.sendMessage(sender, receivers, message);
        event.printEvent();
        return event;
    }




    /**
     * This method invokes a method in game, which does the necessary actions for the next round.
     * If the game state is ENDING, then the last rounds are done. After that, endGame is invoked.
     * we have decided that is the controller the one that manages the changing of turn
     * @return the event "OK" when the current player has been changed
     */
    public Event nextRound() throws RemoteException {
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        try {
            this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        event = this.SRMIInterface.nextRound();
        event.printEvent();
        return event;
    }




    /**
     * This method ends the game. It sets the game state to ENDED, checks all the objectives (2 common objs
     * and 1 personalObj) and adds the points to the correct player.
     * Finally, it checks the winner (or winners) of the game, and puts them in a list called "winners"
     * @return the event "OK" when all the procedure about the ending of the game has
     * been successfully completed
     */
    public Event endGame() throws RemoteException {
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        try {
            this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        event = this.SRMIInterface.endGame();
        event.printEvent();
        return event;
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
