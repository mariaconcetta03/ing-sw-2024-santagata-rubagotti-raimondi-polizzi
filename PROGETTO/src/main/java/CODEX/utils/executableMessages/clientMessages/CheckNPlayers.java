package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class CheckNPlayers implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        /*
         case CHECK_N_PLAYERS -> { //farà partire gli update che dicono che il gioco è iniziato
                try {
                    gameController.checkNPlayers(); //bisogna aggiungere qui dentro un'eccezione per dire 'game already started'
                    writeTheStream(new SCKMessage(null, Event.OK));
                } catch (RemoteException e) { //da cancellare perchè serve solo ad rmi
                    throw new RuntimeException(e);
                }
            }
         */

    }
}
