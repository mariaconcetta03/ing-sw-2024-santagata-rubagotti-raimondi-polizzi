package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;

/**
 * This class is used to represent a message sent by the server in response to a ping message
 */
public class ServerPong implements ServerMessage{

    @Override
    public void execute(ClientSCK clientSCK){
        clientSCK.setPongReceived(true);
    }
}
