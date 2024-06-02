package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;

import java.rmi.RemoteException;

public class updateLastMovesEvent implements Event{
    private int lastMoves;
    public updateLastMovesEvent(int lastMoves){
        this.lastMoves=lastMoves;
    }
    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateLastMoves(lastMoves);
    }

    @Override
    public void executeSCK(ClientGeneralInterface client) {

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
