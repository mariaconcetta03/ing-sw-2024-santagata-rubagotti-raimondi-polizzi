package distributed;

import org.model.*;
import utils.Event;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientGeneralInterface extends Remote {
    Event addPlayerToLobby (Player p, int gameId) throws RemoteException, NotBoundException;
    Event chooseNickname (Player chooser, String nickname) throws RemoteException, NotBoundException;
    Event createLobby (Player creator, int numOfPlayers) throws RemoteException, NotBoundException;
    Event createGame (List<Player> gamePlayers) throws RemoteException, NotBoundException;
    Event addPlayerToGame (Player player) throws RemoteException;
    Event startGame() throws RemoteException;
    Event playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException;
    Event playBaseCard (String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException;
    Event drawCard(String nickname, PlayableCard selectedCard) throws RemoteException;
    Event chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard) throws RemoteException;
    Event choosePawnColor(Player chooser, Pawn selectedColor) throws RemoteException;
    Event sendMessage(Player sender, List<Player> receivers, String message) throws RemoteException;
    Event nextRound() throws RemoteException;
    Event endGame() throws RemoteException;
}
