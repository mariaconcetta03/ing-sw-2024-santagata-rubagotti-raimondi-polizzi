package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

/**
 * This class is used to represent a message which is sent when a client wants to create a new lobby
 */
public class CreateLobby implements ClientMessage {
    private final String creatorNickname;
    private final int numOfPlayers;



    /**
     * This method is used to create a new lobby
     * @param creatorNickname player creator
     * @param numOfPlayers in the lobby
     */
    public CreateLobby(String creatorNickname, int numOfPlayers) {
        this.creatorNickname = creatorNickname;
        this.numOfPlayers = numOfPlayers;
    }



    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.createLobby(creatorNickname, numOfPlayers);


    }
}
