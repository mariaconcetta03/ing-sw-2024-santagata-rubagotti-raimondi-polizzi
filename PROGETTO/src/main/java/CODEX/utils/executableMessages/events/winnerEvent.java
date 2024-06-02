package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.Player;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.rmi.RemoteException;
import java.util.List;

public class winnerEvent implements Event {
    List<Player> winners;
    public winnerEvent(List<Player> winners){
        this.winners=winners;
    }
    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.showWinner(winners);
    }

    @Override
    public void executeSCK(ClientGeneralInterface client) {

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
