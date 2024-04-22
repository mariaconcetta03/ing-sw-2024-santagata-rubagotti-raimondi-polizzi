package distributed;

import utils.Event;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientGeneralInterface extends Remote {
    Event addPlayerToLobby (Player p, int gameId);
    Event chooseNickname (Player chooser, String nickname);
    Event createLobby (Player creator, int numOfPlayers);
    Event addPlayerToGame (Player player);
    Event startGame();
    Event playCard(PlayableCard selectedCard, Coordinates position, boolean orientation);
    Event drawCard(String nickname, PlayableCard selectedCard);
    Event chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard);
    Event sendMessage(Player sender, List<Player> receivers, String message);
    Event nextRound();
    Event endGame();
}
