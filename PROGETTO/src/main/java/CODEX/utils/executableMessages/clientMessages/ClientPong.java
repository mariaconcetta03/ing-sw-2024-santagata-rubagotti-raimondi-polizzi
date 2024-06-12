package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

/**
 * This class is used to represent a message which is the pong, a message useful for detecting disconnections
 */
public class ClientPong implements ClientMessage {
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.setPongReceived(true);
    }
}
