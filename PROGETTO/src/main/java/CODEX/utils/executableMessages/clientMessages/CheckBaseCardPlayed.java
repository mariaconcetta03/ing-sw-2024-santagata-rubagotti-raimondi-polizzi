package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

import java.rmi.RemoteException;

/**
 * This class is used to represent a message which is sent when a client chooses a base card
 */
public class CheckBaseCardPlayed implements ClientMessage{
    /**
     * Class constructor (default)
     */
    public CheckBaseCardPlayed(){}



    /**
     * This method checks if all the players have correctly played their base card
     * If they all did this, then it starts some updates to start the game
     * @param clientHandlerThread thread
     */
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {

            clientHandlerThread.getGameController().checkBaseCardPlayed();

    }
}
