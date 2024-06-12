package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

import java.rmi.RemoteException;

/**
 * This class is used to represent a message which is sent when checking the objective cards chosen by the players
 */
public class CheckObjectiveCardChosen implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        try {
            clientHandlerThread.getGameController().checkObjectiveCardChosen();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
