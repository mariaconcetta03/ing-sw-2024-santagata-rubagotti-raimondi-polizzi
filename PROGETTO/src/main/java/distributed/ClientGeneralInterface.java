package distributed;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import org.model.*;
import utils.Observer;


import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
   
public interface ClientGeneralInterface extends Remote, ClientActionsInterface {

    //inizio update
    void updateBoard (Board board) throws RemoteException;
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
