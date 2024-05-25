package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;

import java.util.List;

public class ServerAvailableLobbies implements ServerMessage{
    public ServerAvailableLobbies(List<Object>list){

    }
    @Override
    public void execute(ClientSCK clientSCK){
        /*
        case AVAILABLE_LOBBY -> {
                synchronized (actionLock) {
                    for(Object o: sckMessage.getObj()){
                        lobbyId.add((Integer) o);
                    }
                    responseReceived = true;
                    actionLock.notify();
                }
            }
         */

    }
}
