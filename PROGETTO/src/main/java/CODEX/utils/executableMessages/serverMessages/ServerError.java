package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;
import CODEX.utils.ErrorsAssociatedWithExceptions;

public class ServerError implements ServerMessage{
    private final ErrorsAssociatedWithExceptions event;
    public ServerError(ErrorsAssociatedWithExceptions event){
        this.event=event;
    }
    @Override
    public void execute(ClientSCK clientSCK) {
        synchronized (clientSCK.actionLock){ //stampiamo l'errore e poi permettiamo al client di proseguire
            clientSCK.setErrorState(true);
            System.out.println(this.event.toString());
            clientSCK.setResponseReceived(true);
            clientSCK.notify();
        }

    }
}
