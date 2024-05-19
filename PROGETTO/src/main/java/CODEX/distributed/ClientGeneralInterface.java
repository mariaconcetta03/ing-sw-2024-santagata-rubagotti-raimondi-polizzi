package CODEX.distributed;

import CODEX.org.model.*;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientGeneralInterface extends Remote, ClientActionsInterface {

    //inizio update
    void updateBoard (String boardOwner, Board board) throws RemoteException;
    void updateResourceDeck (PlayableDeck resourceDeck) throws RemoteException;
    void updateGoldDeck (PlayableDeck goldDeck) throws RemoteException;
    void updatePlayerDeck (String playerNickname, PlayableCard[] playerDeck)  throws RemoteException;
    void updateResourceCard1(PlayableCard card) throws RemoteException;
    void updateResourceCard2(PlayableCard card) throws RemoteException ;
    void updateGoldCard1(PlayableCard card) throws RemoteException;
    void updateGoldCard2(PlayableCard card) throws RemoteException;
    void updateChat(Chat chat) throws RemoteException;
    void updatePawns(Player player, Pawn pawn) throws RemoteException;
    void updateNickname(Player player, String nickname) throws RemoteException;
    void updateRound(Player newCurrentPlayer) throws RemoteException;
    void updateGameState(Game.GameState  gameState) throws RemoteException;
    //fine update


}
