package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;

public class ServerOk implements ServerMessage{
    private final Integer gameId;

    public ServerOk(){
        System.out.println("sono in ServerOk()");
        this.gameId=null;
    }
    public ServerOk(Integer gameId){
        this.gameId=gameId;
    }
    @Override
    public void execute(ClientSCK clientSCK){
        System.out.println("sono in execute di ServerOk");
        synchronized (clientSCK.actionLock){
            System.out.println("sono un ClientSCK e mi è arrivato un ServerOk");
            if(this.gameId!=null){ //we enter here if this ServerOk has been sent after addPlayerToLobby or CreateLobby
                clientSCK.setGameID(this.gameId);
            }
            clientSCK.setResponseReceived(true);
            clientSCK.actionLock.notify();
        }

    }
}
