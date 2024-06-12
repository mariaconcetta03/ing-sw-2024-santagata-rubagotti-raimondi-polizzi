package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;
import CODEX.distributed.messages.SCKMessage;
import CODEX.utils.executableMessages.clientMessages.ClientMessage;
import CODEX.utils.executableMessages.clientMessages.ClientPong;
import java.io.IOException;

/**
 * This class is used to represent a message sent by the server to ask the client if he is connected
 */
public class ServerPing implements ServerMessage{

    @Override
    public void execute(ClientSCK clientSCK){
        ClientMessage clientMessage=new ClientPong();
        try {
            clientSCK.sendMessage(new SCKMessage(clientMessage));
        } catch (IOException ignored) {

        }
    }
}
