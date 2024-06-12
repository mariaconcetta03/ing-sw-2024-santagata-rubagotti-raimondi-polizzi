package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.Player;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This event is useful to communicate that the playing order has changed
 */
public class updatePlayersOrderEvent implements Event {
    private List<Player> newPlayingOrder;

    public updatePlayersOrderEvent(List<Player> newPlayingOrder) {
        this.newPlayingOrder = newPlayingOrder;
    }
    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateRound(newPlayingOrder);
        return false;
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateRound(newPlayingOrder);
        } catch (RemoteException ignored) {
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
