package CODEX.utils.executableMessages;

import CODEX.distributed.ClientActionsInterface;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.PlayableDeck;

import java.rmi.RemoteException;

public class updateResourceDeckEvent implements Event{
    private PlayableDeck resourceDeck;
    public updateResourceDeckEvent(PlayableDeck resourceDeck){
        this.resourceDeck=resourceDeck;
    }
    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
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
    public boolean executeSCKServerSide(ClientActionsInterface client) { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
