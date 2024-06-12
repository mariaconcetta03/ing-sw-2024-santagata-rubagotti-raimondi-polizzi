package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.distributed.messages.SCKMessage;
import CODEX.utils.executableMessages.serverMessages.ServerMessage;
import CODEX.utils.executableMessages.serverMessages.ServerPong;

/**
 * This class is used to represent a ping, a message useful for detecting disconnections
 */
public class ClientPing implements ClientMessage {
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        ServerMessage serverMessage = new ServerPong();
        clientHandlerThread.writeTheStream(new SCKMessage(serverMessage)); // to tell the client 'yes, I'm still connected'
    }
}
