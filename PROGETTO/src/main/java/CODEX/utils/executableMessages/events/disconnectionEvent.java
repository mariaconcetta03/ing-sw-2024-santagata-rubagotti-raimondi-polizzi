package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;

import java.rmi.RemoteException;

public class disconnectionEvent implements Event {  //sostituisce quello che prima era l'evento di GAME_LEFT
    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        //client.
    }

    @Override
    public void executeSCK(ClientGeneralInterface client) {
        //to be decided
    }

    @Override
    public boolean executeSCKServerSide( ) { //returns true when we are considering updateGameState and the new state is 'STARTED'
       return false;

    }
}
