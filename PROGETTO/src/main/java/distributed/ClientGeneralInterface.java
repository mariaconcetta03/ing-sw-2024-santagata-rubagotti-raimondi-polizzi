package distributed;

import utils.Event;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientGeneralInterface extends Remote {
    Event createLobby() throws RemoteException, NotBoundException;
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
