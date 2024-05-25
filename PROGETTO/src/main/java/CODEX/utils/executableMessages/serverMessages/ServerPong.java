package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;

public class ServerPong implements ServerMessage{ //sent by the server to say 'yes, I'm still connected' (in response to a ping message)
    @Override
    public void execute(ClientSCK clientSCK){
        System.out.println("sono il client e mi è arrivato un pong");
        clientSCK.setPongReceived(true);
    }
}
