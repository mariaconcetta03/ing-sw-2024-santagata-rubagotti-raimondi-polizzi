package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

/**
 * This class is used to represent a message which is the pong, a message useful for detecting disconnections
 */
public class ClientPong implements ClientMessage {

    /**
     * Default constructor
     */
    public ClientPong() {

    }


    /**
     * This method sets "PongReceived" to true
     *
     * @param clientHandlerThread thread
     */
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.setPongReceived(true);
    }
}
