package distributed;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import org.model.*;


import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
   
public interface ClientGeneralInterface extends Remote {
    void addPlayerToLobby (Player p, int gameId) throws RemoteException, NotBoundException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException;
    void chooseNickname (Player chooser, String nickname) throws RemoteException, NotBoundException, NicknameAlreadyTakenException;
    void createLobby (Player creator, int numOfPlayers) throws RemoteException, NotBoundException;
    void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, NotBoundException;
    void playBaseCard (String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException, NotBoundException;
    void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException, NotBoundException;
    void chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard) throws RemoteException, NotBoundException;
    void choosePawnColor(Player chooser, Pawn selectedColor) throws RemoteException, NotBoundException;
    void sendMessage(Player sender, List<Player> receivers, String message) throws RemoteException, NotBoundException;
    void leaveGame(String nickname) throws RemoteException, NotBoundException, IllegalArgumentException;
    void SRMIInterfaceFromRegistry() throws RemoteException, NotBoundException;

    }
