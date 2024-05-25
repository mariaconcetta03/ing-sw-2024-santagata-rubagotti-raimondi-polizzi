package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class AddPlayerToLobby implements ClientMessage{

    private final String playerNickname;
    private final int gameId;


    public AddPlayerToLobby(String playerNickname, int gameId){
        this.playerNickname=playerNickname;
        this.gameId=gameId;
    }
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.addPlayerToLobby(this.playerNickname,this.gameId);
    }
}
