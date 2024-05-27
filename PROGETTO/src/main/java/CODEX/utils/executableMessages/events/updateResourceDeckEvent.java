package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.PlayableDeck;

import java.rmi.RemoteException;

public class updateResourceDeckEvent implements Event {
    private PlayableDeck resourceDeck;
    public updateResourceDeckEvent(PlayableDeck resourceDeck){
        this.resourceDeck=resourceDeck;
    }
    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateResourceDeck(resourceDeck);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateResourceDeck(resourceDeck);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
