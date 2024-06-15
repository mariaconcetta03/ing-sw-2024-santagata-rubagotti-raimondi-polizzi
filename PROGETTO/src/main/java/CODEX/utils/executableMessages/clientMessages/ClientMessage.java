package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import java.io.Serializable;


public interface ClientMessage extends Serializable {
    /**
     * This method is used to execute an action
     * @param clientHandlerThread thread
     */
    void execute(ClientHandlerThread clientHandlerThread);

}
