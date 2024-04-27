package distributed.RMI;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import org.model.*;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerRMIInterface extends Remote {
    void startServer() throws RemoteException;

    void createLobby(Player creator, int numOfPlayers) throws RemoteException;

    void addPlayerToLobby(Player p, int gameId) throws RemoteException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException;

    void chooseNickname(Player chooser, String nickname) throws RemoteException, NicknameAlreadyTakenException;

    void createGame(List<Player> gamePlayers) throws RemoteException;

    void addPlayerToGame(Player player) throws RemoteException, ArrayIndexOutOfBoundsException;

    void startGame() throws RemoteException, IllegalStateException;

    void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException;

    void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException;

    void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException;

    void chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard) throws RemoteException;

    void choosePawnColor(Player chooser, Pawn selectedColor) throws RemoteException;

    void sendMessage(Player sender, List<Player> receivers, String message) throws RemoteException;

    void nextRound() throws RemoteException;

    void endGame() throws RemoteException;

    void leaveGame(String nickname) throws RemoteException, IllegalArgumentException;

    void setGameController(Player caller) throws RemoteException;
}