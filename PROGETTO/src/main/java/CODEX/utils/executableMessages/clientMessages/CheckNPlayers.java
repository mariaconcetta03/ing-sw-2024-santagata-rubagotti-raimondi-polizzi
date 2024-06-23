package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.distributed.messages.SCKMessage;
import CODEX.utils.executableMessages.serverMessages.ServerMessage;
import CODEX.utils.executableMessages.serverMessages.ServerOk;


/**
 * This class is used to represent a message which is sent when checking the number of players that have to join a game
 */
public class CheckNPlayers implements ClientMessage {

    /**
     * Default constructor
     */
    public CheckNPlayers() {
    }


    /**
     * This method checks if all the N players have joined the lobby
     *
     * @param clientHandlerThread thread
     */
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {

        clientHandlerThread.getGameController().checkNPlayers();

        ServerMessage serverMessage = new ServerOk();
        clientHandlerThread.writeTheStream(new SCKMessage(serverMessage));
    }
}
