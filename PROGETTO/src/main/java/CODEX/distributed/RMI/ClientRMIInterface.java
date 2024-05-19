package CODEX.distributed.RMI;

import CODEX.org.model.*;
import CODEX.utils.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientRMIInterface extends Remote {
    void update() throws RemoteException;

    void updateBoard(String boardOwner, Board obj)throws RemoteException;

    void updateResourceDeck(PlayableDeck obj)throws RemoteException;

    void updateGoldDeck(PlayableDeck obj)throws RemoteException;

    void updatePlayerDeck(String playerNickname, PlayableCard[] playableCards)throws RemoteException;

    void updateResourceCard1(PlayableCard obj)throws RemoteException;

    void updateResourceCard2(PlayableCard obj)throws RemoteException;

    void updateGoldCard1(PlayableCard obj)throws RemoteException;

    void updateGoldCard2(PlayableCard obj)throws RemoteException;

    void updateChat(Chat obj)throws RemoteException;

    void updatePawns(Player player, Pawn pawn)throws RemoteException;

    void updateNickname(Player player, String string)throws RemoteException;

    void updateRound(List<Player> obj)throws RemoteException;

    void updateGameState(Game.GameState obj) throws RemoteException;
    void showError(Event event) throws RemoteException;
    void updateCommonObjectives(ObjectiveCard card1, ObjectiveCard card2) throws RemoteException;
    void updatePersonalObjective(ObjectiveCard card, String nickname) throws RemoteException;
    void finishedSetupPhase2() throws RemoteException;
    void gameLeft() throws RemoteException;
}
