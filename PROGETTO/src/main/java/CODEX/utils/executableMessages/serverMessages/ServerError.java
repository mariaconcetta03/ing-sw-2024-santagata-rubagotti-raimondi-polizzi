package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;
import CODEX.utils.Event; //questa è la enum: bisogna cambiare nome per non confondersi

public class ServerError implements ServerMessage{
    private final Event event;
    public ServerError(Event event){
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
