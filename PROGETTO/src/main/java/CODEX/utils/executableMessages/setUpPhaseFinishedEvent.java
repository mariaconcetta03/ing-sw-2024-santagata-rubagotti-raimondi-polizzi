package CODEX.utils.executableMessages;

import CODEX.distributed.ClientGeneralInterface;

import java.rmi.RemoteException;

public class setUpPhaseFinishedEvent implements Event{
    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.finishedSetUpPhase();
    }
}
