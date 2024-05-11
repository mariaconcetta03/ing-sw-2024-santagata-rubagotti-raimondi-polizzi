package distributed.RMI;

import org.model.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameControllerInterface extends Remote {

    int getId() throws RemoteException;

    Game getGame()throws RemoteException;

    void addRMIClient(ClientRMIInterface rmiClient)throws RemoteException;

    void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation)throws RemoteException;

    void drawCard(String nickname, PlayableCard selectedCard)throws RemoteException;

    void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard)throws RemoteException;

    void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation)throws RemoteException;

    void choosePawnColor(String chooserNickname, Pawn selectedColor)throws RemoteException;

    void sendMessage(String senderNickname, List<String> receiversNicknames, String message)throws RemoteException;

    void leaveGame(String nickname)throws RemoteException;

    void setCheck(int i)throws RemoteException;

    int getCheck()throws RemoteException;
}
