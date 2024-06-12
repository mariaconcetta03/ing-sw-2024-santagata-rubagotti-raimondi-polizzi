package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.distributed.messages.SCKMessage;
import CODEX.org.model.Pawn;
import CODEX.utils.executableMessages.serverMessages.ServerAvailableColors;
import CODEX.utils.executableMessages.serverMessages.ServerMessage;
import java.util.List;

/**
 * This class is used to represent a message which is sent when a client wants to check the available
 * pawn colors in a specific moment
 */
public class ClientAvailableColors implements ClientMessage {
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        List<Pawn> availableColors = clientHandlerThread.getGameController().getGame().getAvailableColors();
        ServerMessage serverMessage = new ServerAvailableColors(availableColors);
        clientHandlerThread.writeTheStream(new SCKMessage(serverMessage));
    }
}
