package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.PlayableDeck;

import java.rmi.RemoteException;

/**
 * This event is useful to communicate that gold deck has changed
 */
public class UpdateGoldDeckEvent implements Event {
    /**
     * Constructor method
     * @param goldDeck is the card to be updated.
     */
    public UpdateGoldDeckEvent(PlayableDeck goldDeck){
        this.goldDeck=goldDeck;
    }
    private PlayableDeck goldDeck;

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateGoldDeck(goldDeck);
        return false;
    }
    @Override
    public void executeSCK(ClientSCK client) {

            client.updateGoldDeck(goldDeck);

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;

    }
}
