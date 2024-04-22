package distributed.RMI;

import org.model.Coordinates;
import org.model.ObjectiveCard;
import org.model.PlayableCard;
import org.model.Player;
import utils.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerRMIInterface extends Remote {
    void startServer() throws RemoteException;

    //void sendEvent(Event event) throws RemoteException;

    Event createLobby (Player creator, int numOfPlayers) throws RemoteException;
    Event addPlayerToLobby (Player p, int gameId) throws RemoteException;
    Event chooseNickname (Player chooser, String nickname) throws RemoteException;
    Event createGame (List<Player> gamePlayers) throws RemoteException;
    Event addPlayerToGame (Player player) throws RemoteException;
    Event startGame() throws RemoteException;
    Event playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException;
    Event drawCard(String nickname, PlayableCard selectedCard) throws RemoteException;
    Event chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard) throws RemoteException;
    Event sendMessage(Player sender, List<Player> receivers, String message) throws RemoteException;
    Event nextRound() throws RemoteException;
    Event endGame() throws RemoteException;
}
