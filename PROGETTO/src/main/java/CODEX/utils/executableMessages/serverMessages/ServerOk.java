package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;

/**
 * This class is used to represent a message sent by the server to send a response of "OK"
 */
public class ServerOk implements ServerMessage {
    private final Integer gameId;



    /**
     * Class constructor default
     */
    public ServerOk() {
        this.gameId = null;
    }



    /**
     * Class constructor
     * @param gameId game ID
     */
    public ServerOk(Integer gameId) {
        this.gameId = gameId;
    }



    @Override
    public void execute(ClientSCK clientSCK) {
        synchronized (clientSCK.actionLock) {
            if (this.gameId != null) {
                // here if this ServerOk has been sent after addPlayerToLobby or CreateLobby
                clientSCK.setGameID(this.gameId);
            }
            clientSCK.setResponseReceived(true);
            clientSCK.actionLock.notify();
        }
    }

}
