package CODEX.utils.executableMessages.serverMessages;

import CODEX.distributed.Socket.ClientSCK;

public class ServerOk implements ServerMessage{
    @Override
    public void execute(ClientSCK clientSCK){
        /*
        case OK -> { //...potremmo stampare anche il messaggio di ok....
                System.out.println("sono in case OK di ClientSCK");
                //questo if mi serve per i test per memorizzare il gameID
                synchronized (actionLock) {
                    if (sckMessage.getObj() != null) { //per usarlo nel test
                        this.gameID = (Integer) sckMessage.getObj().get(0);
                    }
                    this.responseReceived = true; //in this way the client is free to do the next action
                    actionLock.notify(); //per fermare la wait nei metodi della ClientGeneralInterface
                }
            }
         */

    }
}
