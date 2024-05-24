package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;

import java.io.Serializable;

public interface ServerMessage extends Serializable {
    void execute(ClientSCK clientSCK);
}
