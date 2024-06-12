package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;

import java.util.List;


/**
 * This class is used to represent a message sent by the server, when a client wants to choose a lobby to join
 */
public class ServerAvailableLobbies implements ServerMessage{
    private final List<Integer>list;



    /**
     * Class constructor
     * @param list of the available lobbies that a client can join
     */
    public ServerAvailableLobbies(List<Integer>list){
        this.list = list;
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
