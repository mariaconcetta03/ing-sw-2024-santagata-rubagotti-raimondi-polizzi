package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

import java.rmi.RemoteException;

/**
 * This class is used to represent a message which is sent when a client chooses a pawn color
 */
public class CheckChosenPawnColor implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        try {
            clientHandlerThread.getGameController().checkChosenPawnColor();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
