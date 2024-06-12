package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;

import java.rmi.RemoteException;

/**
 * This event is useful to communicate that the number of
 * last moves remained has changed
 */
public class updateLastMovesEvent implements Event{
    private int lastMoves;
    public updateLastMovesEvent(int lastMoves){
        this.lastMoves=lastMoves;
    }
    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateLastMoves(lastMoves);
        return false;
    }

    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateLastMoves(lastMoves);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
