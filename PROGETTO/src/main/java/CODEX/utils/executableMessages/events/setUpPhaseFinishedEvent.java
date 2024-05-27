package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;

import java.rmi.RemoteException;

public class setUpPhaseFinishedEvent implements Event {
    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.finishedSetUpPhase();
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.finishedSetUpPhase();
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
