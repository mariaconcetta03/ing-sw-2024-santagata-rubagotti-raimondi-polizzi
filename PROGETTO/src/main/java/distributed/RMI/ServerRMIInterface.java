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
   /*
    Event addPlayerToLobby (Player p, int gameId);
    Event chooseNickname (Player chooser, String nickname);
    Event createGame (List<Player> gamePlayers);
    Event addPlayerToGame (Player player);
    Event startGame();
    Event playCard(PlayableCard selectedCard, Coordinates position, boolean orientation);
    Event playBaseCard(PlayableCard selectedCard, Coordinates position, boolean orientation); //necessary adding a method to the controller
    Event drawCard(PlayableCard selectedCard);
    Event chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard);
    Event sendMessage(Player sender, List<Player> receivers, String message);
    Event nextRound();
    Event endGame();
    */
}
