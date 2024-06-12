package CODEX.distributed.RMI;

import CODEX.Exceptions.ColorAlreadyTakenException;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameControllerInterface extends Remote {

    int getId() throws RemoteException;

    Game getGame() throws RemoteException;

    void addRMIClient(String nickname, ClientGeneralInterface rmiClient) throws RemoteException;

    void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException;

    void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException;

    void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) throws RemoteException;

    void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, IllegalArgumentException;

    void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, ColorAlreadyTakenException;

    void sendMessage(String senderNickname, List<String> receiversNicknames, String message) throws RemoteException;

    void checkNPlayers() throws RemoteException;

    void checkBaseCardPlayed() throws RemoteException;

    void checkObjectiveCardChosen() throws RemoteException;

    void checkChosenPawnColor() throws RemoteException;

    List<Player> getGamePlayers() throws RemoteException;

    void heartbeat(String nickname) throws RemoteException;

    void startHeartbeat(String nickname) throws RemoteException;

}
