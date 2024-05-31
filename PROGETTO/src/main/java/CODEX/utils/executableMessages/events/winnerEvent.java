package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.rmi.RemoteException;

public class winnerEvent implements Event {
    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        //client.showWinner() mostra il vincitore e spegne a tutti l'app
    }

    @Override
    public void executeSCK(ClientGeneralInterface client) {

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
