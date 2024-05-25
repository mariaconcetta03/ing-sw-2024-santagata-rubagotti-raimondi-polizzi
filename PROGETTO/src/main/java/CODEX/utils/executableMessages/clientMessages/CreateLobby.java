package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class CreateLobby implements ClientMessage{
    private final String creatorNickname;
    private final int numOfPlayers;
    public CreateLobby(String creatorNickname, int numOfPlayers){
        this.creatorNickname=creatorNickname;
        this.numOfPlayers=numOfPlayers;
    }
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.createLobby(creatorNickname,numOfPlayers);


    }
}
