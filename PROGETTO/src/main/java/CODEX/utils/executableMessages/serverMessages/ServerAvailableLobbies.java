package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;

import java.util.List;

public class ServerAvailableLobbies implements ServerMessage{
    private final List<Integer>list;
    public ServerAvailableLobbies(List<Integer>list){
        this.list=list;
    }
    @Override
    public void execute(ClientSCK clientSCK){
        synchronized(clientSCK.actionLock){
            clientSCK.setLobbyId(list);
            clientSCK.setResponseReceived(true);
            clientSCK.actionLock.notify();
        }

    }
}
