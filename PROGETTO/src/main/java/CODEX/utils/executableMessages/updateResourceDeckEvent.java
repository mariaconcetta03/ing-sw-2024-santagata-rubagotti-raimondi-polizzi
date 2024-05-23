package CODEX.utils.executableMessages;

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
}
