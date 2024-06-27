package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;
import CODEX.utils.ErrorsAssociatedWithExceptions;


/**
 * This class is used to represent a message sent by the server, when there's a error
 */
public class ServerError implements ServerMessage {
    private final ErrorsAssociatedWithExceptions event;


    /**
     * Class constructor
     *
     * @param event that causes an error
     */
    public ServerError(ErrorsAssociatedWithExceptions event) {
        this.event = event;
    }


    @Override
    public void execute(ClientSCK clientSCK) {
        synchronized (clientSCK.getActionLock()) {
            clientSCK.setErrorState(true);
            clientSCK.setResponseReceived(true);
            clientSCK.getActionLock().notify();
        }
    }
}
