package CODEX.utils.executableMessages;

import CODEX.distributed.ClientActionsInterface;
import CODEX.distributed.ClientGeneralInterface;

import java.rmi.RemoteException;
import java.util.List;

public class disconnectionEvent implements Event{
    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        //client.
    }

    @Override
    public void executeSCK(ClientGeneralInterface client) {
        //to be decided
    }

    @Override
    public boolean executeSCKServerSide(ClientActionsInterface client) { //returns true when we are considering updateGameState and the new state is 'STARTED'
       return false;

    }
}
