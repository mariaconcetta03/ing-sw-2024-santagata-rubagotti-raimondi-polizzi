package CODEX.distributed;

import CODEX.org.model.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * This interface is useful for describing all the methods which a client can invoke for performing actions in the game
 */
public interface ClientGeneralInterface extends Remote, ClientActionsInterface {

    /**
     * This is an update method
     * @param board the new board we want to update
     * @param boardOwner is the player which possesses the board
     * @param newCard the last card placed
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updateBoard(String boardOwner, Board board, PlayableCard newCard) throws RemoteException;



    /**
     * This is an update method
     * @param resourceDeck the new deck we want to update
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updateResourceDeck(PlayableDeck resourceDeck) throws RemoteException;



    /**
     * This is an update method
     * @param goldDeck the new deck we want to update
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updateGoldDeck(PlayableDeck goldDeck) throws RemoteException;



    /**
     * This is an update method
     * @param playerNickname the player which deck is updated
     * @param playerDeck     the new deck we want to update
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updatePlayerDeck(String playerNickname, PlayableCard[] playerDeck) throws RemoteException;



    /**
     * This is an update method
     * @param card which needs to be updated
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updateResourceCard1(PlayableCard card) throws RemoteException;



    /**
     * This is an update method
     * @param card which needs to be updated
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updateResourceCard2(PlayableCard card) throws RemoteException;



    /**
     * This is an update method
     * @param card which needs to be updated
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updateGoldCard1(PlayableCard card) throws RemoteException;



    /**
     * This is an update method
     * @param card which needs to be updated
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updateGoldCard2(PlayableCard card) throws RemoteException;



    /**
     * This is an update method
     * @param chatIdentifier ID
     * @param chat which needs to be updated
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updateChat(Integer chatIdentifier, Chat chat) throws RemoteException;



    /**
     * This is an update method
     * @param nickname is the nickname of the player who selected a new pawn color
     * @param pawn     is the selected color
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updatePawns(String nickname, Pawn pawn) throws RemoteException;



    /**
     * This is an update method
     * @param newPlayingOrder are the players of the game ordered
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updateRound(List<Player> newPlayingOrder) throws RemoteException;



    /**
     * This is an update method
     * @param gameState is the new Game state (WAITING_FOR_START -> STARTED -> ENDING -> ENDED)
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updateGameState(Game.GameState gameState) throws RemoteException;



    /**
     * This is an update method
     * @param objCard1 is the first common Objective
     * @param objCard2 is the second common Objective
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updateCommonObjectives(ObjectiveCard objCard1, ObjectiveCard objCard2) throws RemoteException;



    /**
     * This is an update method
     * @param personalObjective is the personal objective card
     * @param playerNickname is the owner of the personal objective card
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updatePersonalObjective(ObjectiveCard personalObjective, String playerNickname) throws RemoteException;



    /**
     * This method is called when all the player chose their personal objective card to let the
     * client know the set-up fase has finished.
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void finishedSetUpPhase() throws RemoteException;



    /**
     * This method prints the winners of the game in the TUI
     * @param finalScoreBoard is a Map containing all the players' nicknames as values and as keys their positions
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void showWinner(Map<Integer, List<String>> finalScoreBoard) throws RemoteException;



    /**
     * This is an update method
     * @param lastMoves is the number of turns remaining before the game ends.
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void updateLastMoves(int lastMoves) throws RemoteException;



    /**
     * This method is called when a disconnection happens.
     * It closes the application.
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void handleDisconnection() throws RemoteException;



    /**
     * This method is called when a disconnection happens.
     * It closes the application.
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void heartbeat() throws RemoteException;



    /**
     * This method lets the heartbeat mechanism start
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void startHeartbeat() throws RemoteException;



    /**
     * This method manages disconnections: it does the "exit()" of the client which is disconnecting
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void handleDisconnectionFunction() throws RemoteException;



    /**
     * This method stops the waiting of the RMIClient, which was waiting for an OK message
     * @param nickname of the player
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void okEventExecute(String nickname) throws RemoteException;



    /**
     * Setter method
     * @param b true if response is received, false otherwise
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    void setResponseReceived(boolean b) throws RemoteException;

}
