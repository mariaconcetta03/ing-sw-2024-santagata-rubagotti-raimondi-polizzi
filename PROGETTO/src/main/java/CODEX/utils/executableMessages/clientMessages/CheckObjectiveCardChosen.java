package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

import java.rmi.RemoteException;

/**
 * This class is used to represent a message which is sent when checking the objective cards chosen by the players
 */
public class CheckObjectiveCardChosen implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {

            clientHandlerThread.getGameController().checkObjectiveCardChosen();

    }
}
