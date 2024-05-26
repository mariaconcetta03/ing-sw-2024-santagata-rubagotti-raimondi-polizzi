package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;

public class ServerOk implements ServerMessage{
    private final Integer gameId;

    public ServerOk(){
        this.gameId=null;
    }
    public ServerOk(Integer gameId){
        this.gameId=gameId;
    }
    @Override
    public void execute(ClientSCK clientSCK){
        synchronized (clientSCK.actionLock){
            if(this.gameId!=null){ //we enter here if this ServerOk has been sent after addPlayerToLobby or CreateLobby
                clientSCK.setGameID(this.gameId);
            }
            clientSCK.setResponseReceived(true);
            clientSCK.actionLock.notify();
        }

    }
}
