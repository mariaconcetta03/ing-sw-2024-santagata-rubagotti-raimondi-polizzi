package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class ClientPong implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.setPongReceived(true); //sent by the client to say 'yes, I'm still connected?'
    }
}
