package distributed;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import Exceptions.NicknameAlreadyTakenException;
import org.model.*;
import utils.Observer;


import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
   
public interface ClientGeneralInterface extends Remote {
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
    void SRMIInterfaceFromRegistry() throws RemoteException, NotBoundException;
    void updateBoard (Board board);
    void updateResourceDeck (PlayableDeck resourceDeck);
    void updateGoldDeck (PlayableDeck goldDeck);
    void updatePlayerDeck (Player player, PlayableCard[] playerDeck);
    void updateResourceCard1(PlayableCard card);
    void updateResourceCard2(PlayableCard card);
    void updateGoldCard1(PlayableCard card);
    void updateGoldCard2(PlayableCard card);
    void updateChat(Chat chat);
    void updatePawns(Player player, Pawn pawn);
    void updateNickname(Player player, String nickname);
    void updateRound(Player newCurrentPlayer);
    void updateGameState(Game game);


}
