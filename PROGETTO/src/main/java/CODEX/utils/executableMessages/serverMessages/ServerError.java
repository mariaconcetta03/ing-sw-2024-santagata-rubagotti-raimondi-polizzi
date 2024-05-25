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
        /*
        default -> { //qui ci finiscono tutti i messaggi di errore
                //stampiamo l'errore e poi permettiamo al client di proseguire
                synchronized (actionLock) {
                    errorState=true;
                    System.out.println(sckMessage.getMessageEvent().toString());
                    this.responseReceived = true;
                    actionLock.notify();
                }
            }
         */

    }
}
