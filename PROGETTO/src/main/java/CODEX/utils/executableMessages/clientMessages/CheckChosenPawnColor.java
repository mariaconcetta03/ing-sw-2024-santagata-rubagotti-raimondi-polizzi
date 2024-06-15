package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

import java.rmi.RemoteException;

/**
 * This class is used to represent a message which is sent when a client chooses a pawn color
 */
public class CheckChosenPawnColor implements ClientMessage{

    /**
     * Default constructor
     */
    public CheckChosenPawnColor(){}



    /**
     * This method checks if all player have chosen a pawn
     * @param clientHandlerThread thread
     */
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {

            clientHandlerThread.getGameController().checkChosenPawnColor();

    }
}
