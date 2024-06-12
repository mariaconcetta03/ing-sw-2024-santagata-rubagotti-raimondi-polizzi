package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

/**
 * This class is used to represent a message which is sent when a client wants to join a lobby
 */
public class AddPlayerToLobby implements ClientMessage{

    private final String playerNickname;
    private final int gameId;

    /**
     * Class constructor
     * @param playerNickname who wants to join the lobby
     * @param gameId to which the player wants to join
     */
    public AddPlayerToLobby(String playerNickname, int gameId){
        this.playerNickname = playerNickname;
        this.gameId = gameId;
    }
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.addPlayerToLobby(this.playerNickname,this.gameId);
    }
}
