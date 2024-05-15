package distributed.RMI;

import org.model.*;
import utils.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientRMIInterface extends Remote {
    void update() throws RemoteException;

    void updateBoard(Board obj)throws RemoteException;

    void updateResourceDeck(PlayableDeck obj)throws RemoteException;

    void updateGoldDeck(PlayableDeck obj)throws RemoteException;

    void updatePlayerDeck(Player player, PlayableCard[] playableCards)throws RemoteException;

    void updateResourceCard1(PlayableCard obj)throws RemoteException;

    void updateResourceCard2(PlayableCard obj)throws RemoteException;

    void updateGoldCard1(PlayableCard obj)throws RemoteException;

    void updateGoldCard2(PlayableCard obj)throws RemoteException;

    void updateChat(Chat obj)throws RemoteException;

    void updatePawns(Player player, Pawn pawn)throws RemoteException;

    void updateNickname(Player player, String string)throws RemoteException;

    void updateRound(List<Player> obj)throws RemoteException;

    void updateGameState(Game obj) throws RemoteException;
    void showError(Event event) throws RemoteException;
}
