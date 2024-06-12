package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;

import java.rmi.RemoteException;

/**
 * This event is useful to communicate that the game can start because all the players have all that is need to start the game
 */
public class setUpPhaseFinishedEvent implements Event {
    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.finishedSetUpPhase();
        return false;
    }
    @Override
    public void executeSCK(ClientSCK client) {


            client.finishedSetUpPhase();



    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
