package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.distributed.messages.SCKMessage;
import CODEX.utils.executableMessages.serverMessages.ServerAvailableLobbies;
import CODEX.utils.executableMessages.serverMessages.ServerMessage;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to represent a message which is sent when a client wants to check the available lobbies in a
 * specific moment
 */
public class ClientAvailableLobbies implements ClientMessage {
    /**
     * Default constructor
     */
    public ClientAvailableLobbies(){}



    /**
     * This method checks what lobbies are available in a specific moment
     * @param clientHandlerThread thread
     */
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        List<Integer> list = new ArrayList<>();
        try {
            list.addAll(clientHandlerThread.getServerController().getAvailableGameControllersId());
        } catch (RemoteException ignored) {
        }
        ServerMessage serverMessage = new ServerAvailableLobbies(list);
        clientHandlerThread.writeTheStream(new SCKMessage(serverMessage));

    }
}
