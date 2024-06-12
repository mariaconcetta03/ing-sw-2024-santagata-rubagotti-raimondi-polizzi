package CODEX.distributed;

import CODEX.org.model.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface ClientGeneralInterface extends Remote, ClientActionsInterface {

    void updateBoard(String boardOwner, Board board, PlayableCard newCard) throws RemoteException;

    void updateResourceDeck(PlayableDeck resourceDeck) throws RemoteException;

    void updateGoldDeck(PlayableDeck goldDeck) throws RemoteException;

    void updatePlayerDeck(String playerNickname, PlayableCard[] playerDeck) throws RemoteException;

    void updateResourceCard1(PlayableCard card) throws RemoteException;

    void updateResourceCard2(PlayableCard card) throws RemoteException;

    void updateGoldCard1(PlayableCard card) throws RemoteException;

    void updateGoldCard2(PlayableCard card) throws RemoteException;

    void updateChat(Integer chatIdentifier, Chat chat) throws RemoteException;

    void updatePawns(String nickname, Pawn pawn) throws RemoteException;

    void updateRound(List<Player> newPlayingOrder) throws RemoteException;

    void updateGameState(Game.GameState gameState) throws RemoteException;

    void updateCommonObjectives(ObjectiveCard objCard1, ObjectiveCard objCard2) throws RemoteException;

    void updatePersonalObjective(ObjectiveCard personalObjective, String playerNickname) throws RemoteException;

    void finishedSetUpPhase() throws RemoteException;

    void showWinner(Map<Integer, List<String>> finalScoreBoard) throws RemoteException;

    void updateLastMoves(int lastMoves) throws RemoteException;

    void handleDisconnection() throws RemoteException;


    void heartbeat() throws RemoteException;

    void startHeartbeat() throws RemoteException;

    void handleDisconnectionFunction() throws RemoteException;

    void setResponseReceived(boolean b) throws RemoteException;

    void okEventExecute(String nickname) throws RemoteException;
}
