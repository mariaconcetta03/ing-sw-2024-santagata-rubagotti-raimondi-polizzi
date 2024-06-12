package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import java.io.Serializable;


public interface ClientMessage extends Serializable {
    void execute(ClientHandlerThread clientHandlerThread);

}
