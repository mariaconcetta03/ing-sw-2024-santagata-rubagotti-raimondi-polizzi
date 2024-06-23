package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;

import java.io.Serializable;

/**
 * This interface is useful for server messages
 */
public interface ServerMessage extends Serializable {

    /**
     * Execute method
     *
     * @param clientSCK client socket
     */
    void execute(ClientSCK clientSCK);
}
