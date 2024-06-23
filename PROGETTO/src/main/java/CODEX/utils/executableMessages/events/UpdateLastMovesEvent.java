package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;

import java.rmi.RemoteException;

/**
 * This event is useful to communicate that the number of
 * last moves remained has changed
 */
public class UpdateLastMovesEvent implements Event{
    private int lastMoves;
    public UpdateLastMovesEvent(int lastMoves){
        this.lastMoves=lastMoves;
    }
    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateLastMoves(lastMoves);
        return false;
    }

    @Override
    public void executeSCK(ClientSCK client) {
            client.updateLastMoves(lastMoves);
    }


    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
