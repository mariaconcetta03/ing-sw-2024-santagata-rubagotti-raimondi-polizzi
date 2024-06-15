package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import java.io.Serializable;

/**
 * This interface represents messages sent by ClientSCK, which is the client side, to communicate with the server side.
 * Every message is useful to invoke a method in the server side and the response would be a server message.
 */
public interface ClientMessage extends Serializable {
    /**
     * This method is used to execute an action
     * @param clientHandlerThread thread
     */
    void execute(ClientHandlerThread clientHandlerThread);

}
