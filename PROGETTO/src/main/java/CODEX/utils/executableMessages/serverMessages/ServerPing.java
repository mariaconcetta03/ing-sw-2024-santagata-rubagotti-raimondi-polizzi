package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;
import CODEX.distributed.messages.SCKMessage;
import CODEX.utils.executableMessages.clientMessages.ClientMessage;
import CODEX.utils.executableMessages.clientMessages.ClientPong;

import java.io.IOException;

public class ServerPing implements ServerMessage{ //sent by the server to say 'are you still connected?'
    @Override
    public void execute(ClientSCK clientSCK){
        ClientMessage clientMessage=new ClientPong();
        try {
            clientSCK.sendMessage(new SCKMessage(clientMessage)); //to say to the server 'yes, I'm still connected'
        } catch (IOException ignored) {

        }
    }
}
