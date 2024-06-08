package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

import java.rmi.RemoteException;

public class CheckBaseCardPlayed implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        try {
            clientHandlerThread.getGameController().checkBaseCardPlayed();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        //non abbiamo bisogno dell'evento di ok perche la funzione checkBaseCardPlayed() del
        //game controller non fa altro che generare update se si è conclusa la scelta delle base cards

    }
}
