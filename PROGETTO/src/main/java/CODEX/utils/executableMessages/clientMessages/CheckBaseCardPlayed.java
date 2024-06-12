package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

import java.rmi.RemoteException;

/**
 * This class is used to represent a message which is sent when a client chooses a base card
 */
public class CheckBaseCardPlayed implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        try {
            clientHandlerThread.getGameController().checkBaseCardPlayed();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
