package CODEX.distributed.RMI;

import CODEX.Exceptions.ColorAlreadyTakenException;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This is the interface about gameControllers methods
 */
public interface GameControllerInterface extends Remote {

    /**
     * Getter method
     * @return the ID of the gameController
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    int getId() throws RemoteException;



    /**
     * Getter method
     * @return the game of the related gameController
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    Game getGame() throws RemoteException;



    /**
     * This method is called remotely by the RMIClient when he asks to create a Lobby or join a Lobby
     * @param nickname is the nickname of the Client
     * @param rmiClient is the client who is joining the game
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void addRMIClient(String nickname, ClientGeneralInterface rmiClient) throws RemoteException;




    /**
     * This method let the Player place the baseCard (in the middle of the table) and, if all the players
     * have placed their baseCard, it let the Game finish the set-up phase giving the last necessary cards
     * @param nickname the player who plays the card
     * @param baseCard the base card played
     * @param orientation of the played card
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException;



    /**
     * This method allows the currentPlayer to draw a card from the decks or from the unveiled ones
     * @param nickname of the player who is going to draw the card
     * @param selectedCard is the Card the Players wants to draw
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException;



    /**
     * This method allows the chooser Player to select his personal ObjectiveCard
     * @param chooserNickname is the nickname of the player selecting the ObjectiveCard
     * @param selectedCard is the ObjectiveCard the player selected
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) throws RemoteException;



    /**
     * This method "plays" the card selected by the Player in his own Board
     * @param nickname is the nickname of the Player that wants to play a card
     * @param selectedCard the Card the Player wants to play
     * @param position the position where the Player wants to play the Card
     * @param orientation the side on which the Player wants to play the Card
     * @throws IllegalArgumentException if there is an error in playing the card
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, IllegalArgumentException;

    void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, ColorAlreadyTakenException;

    void sendMessage(String senderNickname, List<String> receiversNicknames, String message) throws RemoteException;

    void checkNPlayers() throws RemoteException;

    void checkBaseCardPlayed() throws RemoteException;

    void checkObjectiveCardChosen() throws RemoteException;

    void checkChosenPawnColor() throws RemoteException;

    List<Player> getGamePlayers() throws RemoteException;

    void heartbeat(String nickname) throws RemoteException;

    void startHeartbeat(String nickname) throws RemoteException;

}
