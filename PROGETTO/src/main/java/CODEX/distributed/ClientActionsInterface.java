package CODEX.distributed;

import CODEX.Exceptions.FullLobbyException;
import CODEX.Exceptions.GameAlreadyStartedException;
import CODEX.Exceptions.GameNotExistsException;
import CODEX.Exceptions.NicknameAlreadyTakenException;
import CODEX.org.model.Coordinates;
import CODEX.org.model.ObjectiveCard;
import CODEX.org.model.Pawn;
import CODEX.org.model.PlayableCard;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientActionsInterface {
    void addPlayerToLobby (String playerNickname, int gameId) throws RemoteException, NotBoundException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException;
    void chooseNickname (String nickname) throws RemoteException, NotBoundException, NicknameAlreadyTakenException;
    void createLobby (String creatorNickname, int numOfPlayers) throws RemoteException, NotBoundException;
    void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, NotBoundException;
    void playBaseCard (String nickname, PlayableCard baseCard, boolean orientation) throws RemoteException, NotBoundException;
    void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException, NotBoundException;
    void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) throws RemoteException, NotBoundException;
    void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, NotBoundException;
    void sendMessage(String senderNickname, List<String> receiversNickname, String message) throws RemoteException, NotBoundException;
    void leaveGame(String nickname) throws RemoteException, NotBoundException, IllegalArgumentException;
}
