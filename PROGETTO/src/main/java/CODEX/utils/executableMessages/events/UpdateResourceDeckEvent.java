package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.PlayableDeck;
import java.rmi.RemoteException;

/**
 * This event is useful to communicate that resource deck has changed
 */
public class UpdateResourceDeckEvent implements Event {
    private PlayableDeck resourceDeck;
    public UpdateResourceDeckEvent(PlayableDeck resourceDeck){
        this.resourceDeck=resourceDeck;
    }
    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateResourceDeck(resourceDeck);
        return false;
    }
    @Override
    public void executeSCK(ClientSCK client) {

            client.updateResourceDeck(resourceDeck);

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;

    }
}
