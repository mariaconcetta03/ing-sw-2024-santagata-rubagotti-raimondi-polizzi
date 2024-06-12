package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.distributed.messages.SCKMessage;
import CODEX.utils.executableMessages.serverMessages.ServerMessage;
import CODEX.utils.executableMessages.serverMessages.ServerOk;

import java.rmi.RemoteException;

/**
 * This class is used to represent a message which is sent when checking the number of players that have to join a game
 */
public class CheckNPlayers implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        try {
            clientHandlerThread.getGameController().checkNPlayers();
        } catch (RemoteException ignored) {}
        ServerMessage serverMessage=new ServerOk();
        clientHandlerThread.writeTheStream(new SCKMessage(serverMessage));
    }
}
