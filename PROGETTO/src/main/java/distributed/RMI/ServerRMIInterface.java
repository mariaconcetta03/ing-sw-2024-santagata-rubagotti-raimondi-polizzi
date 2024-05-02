package distributed.RMI;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import controller.GameController;
import org.model.*;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerRMIInterface extends Remote {
    void startServer() throws RemoteException;

    GameController createLobby(String creatorNickname, int numOfPlayers) throws RemoteException;

    GameController addPlayerToLobby(String playerNickname, int gameId) throws RemoteException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException;

    void chooseNickname(String nickname) throws RemoteException, NicknameAlreadyTakenException;
}