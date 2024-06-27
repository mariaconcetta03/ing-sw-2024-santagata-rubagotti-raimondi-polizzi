package CODEX.distributed;

import CODEX.Exceptions.*;
import CODEX.org.model.Coordinates;
import CODEX.org.model.ObjectiveCard;
import CODEX.org.model.Pawn;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;
import java.util.List;


/**
 * This interface represents the actions which the clients (RMI and TCP) can perform.
 */
public interface ClientActionsInterface {

    /**
     * This method is used to add a single player to an already created lobby
     *
     * @param playerNickname is the nickname of the player who wants to join the lobby
     * @param gameId         is the lobby the player wants to join
     * @throws GameAlreadyStartedException if the game has already started
     * @throws FullLobbyException          if the lobby is full
     * @throws GameNotExistsException      if the game you're trying to access doesn't exist
     * @throws RemoteException             if an exception happens while communicating with the remote
     */
    void addPlayerToLobby(String playerNickname, int gameId) throws RemoteException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException;


    /**
     * Once connected the player get to choose his nickname that must be different from all the other presents
     *
     * @param nickname is the String he wants to put as his nickname
     * @throws NicknameAlreadyTakenException if the nickname has already been chosen by another user
     * @throws RemoteException               if an exception happens while communicating with the remote
     */
    void chooseNickname(String nickname) throws RemoteException, NicknameAlreadyTakenException;


    /**
     * This method calls the function into the ServerController
     *
     * @param creatorNickname is the nickname of the player who wants to create a new lobby
     * @param numOfPlayers    is the number of player the creator decided can play in the lobby
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void createLobby(String creatorNickname, int numOfPlayers) throws RemoteException;


    /**
     * This method "plays" the card selected by the Player in his own Board
     *
     * @param selectedCard the Card the Player wants to play
     * @param nickname     player's nickname
     * @param position     the position where the Player wants to play the Card
     * @param orientation  the side on which the Player wants to play the Card
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException;


    /**
     * This method let the Player place the baseCard (in an already decided position) and, if all the players
     * have placed their baseCard, it let the game finish the set-up phase giving the last necessary cards
     *
     * @param nickname    is the nickname of the Player that wants to play a card
     * @param baseCard    is the base card that is played
     * @param orientation the side on which the Player wants to play the Card
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException;


    /**
     * This method allows the currentPlayer to draw a card from the decks or from the unveiled ones
     *
     * @param nickname     is the nickname of the player who wants to draw the card
     * @param selectedCard is the Card the Players wants to draw
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException;


    /**
     * This method allows the chooser Player to select his personal ObjectiveCard
     *
     * @param chooserNickname is the nickname of the player selecting the ObjectiveCard
     * @param selectedCard    is the ObjectiveCard the player selected
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) throws RemoteException;


    /**
     * This method allows a player to choose the color of his pawn
     *
     * @param chooserNickname is the nickname of the player who needs to choose the color
     * @param selectedColor   is the color chosen by the player
     * @throws ColorAlreadyTakenException if someone chose the color before you
     * @throws RemoteException            if an exception happens while communicating with the remote
     */
    void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, ColorAlreadyTakenException;


    /**
     * This method sends a message from the sender to the receiver(s)
     *
     * @param senderNickname    is the nickname of the player sending the message
     * @param receiversNickname is the list of nicknames of the players who need to receive this message
     * @param message           is the string sent by the sender to the receivers
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void sendMessage(String senderNickname, List<String> receiversNickname, String message) throws RemoteException;


    /**
     * This method lets a player end the game.
     *
     * @param nickname of the player who is going leave the game
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void leaveGame(String nickname) throws RemoteException;
}
