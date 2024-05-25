package CODEX.distributed.RMI;

import CODEX.Exceptions.FullLobbyException;
import CODEX.Exceptions.GameAlreadyStartedException;
import CODEX.Exceptions.GameNotExistsException;
import CODEX.Exceptions.NicknameAlreadyTakenException;
import CODEX.controller.GameController;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface ServerRMIInterface extends Remote {
    void startServer() throws RemoteException;

    GameControllerInterface createLobby(String creatorNickname, int numOfPlayers) throws RemoteException;

    GameControllerInterface addPlayerToLobby(String playerNickname, int gameId) throws RemoteException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException;

    void chooseNickname(String nickname) throws RemoteException, NicknameAlreadyTakenException;

    Map<Integer, GameController> getAllGameControllers() throws RemoteException;
    Set<Integer> getAvailableGameControllersId() throws RemoteException;
}