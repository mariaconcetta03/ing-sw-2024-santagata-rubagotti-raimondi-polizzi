package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Pawn;

import java.util.List;


/**
 * This class is used to represent a message sent by the server, when a client wants to choose pawn colors
 */
public class ServerAvailableColors implements ServerMessage{
    private final List<Pawn> availableColors;



    /**
     * Class constructor
     * @param availableColors of the pawns
     */
    public ServerAvailableColors(List<Pawn> availableColors){
        this.availableColors=availableColors;
    }



    @Override
    public void execute(ClientSCK clientSCK) {
        synchronized(clientSCK.actionLock){
            //salvo gli available colors sul clientSCK
            clientSCK.setAvailableColors(availableColors);
            clientSCK.setResponseReceived(true);
            clientSCK.actionLock.notify();
        }
    }
}
