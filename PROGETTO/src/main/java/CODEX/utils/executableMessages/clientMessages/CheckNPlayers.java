package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.distributed.messages.SCKMessage;
import CODEX.utils.executableMessages.serverMessages.ServerMessage;
import CODEX.utils.executableMessages.serverMessages.ServerOk;

import java.rmi.RemoteException;

public class CheckNPlayers implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        try {//bisogna aggiungere qui dentro un'eccezione per dire 'game already started'
            clientHandlerThread.getGameController().checkNPlayers(); //farà partire gli update che dicono che il gioco è iniziato
        } catch (RemoteException ignored) {
        }
        ServerMessage serverMessage=new ServerOk();
        clientHandlerThread.writeTheStream(new SCKMessage(serverMessage));


    }
}
