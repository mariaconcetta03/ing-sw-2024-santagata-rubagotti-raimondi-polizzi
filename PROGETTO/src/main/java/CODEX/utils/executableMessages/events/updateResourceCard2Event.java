package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.PlayableCard;
import java.rmi.RemoteException;


/**
 * This event is useful to communicate that resource card 2 has changed
 */
public class updateResourceCard2Event implements Event {
    private PlayableCard card;

    public updateResourceCard2Event(PlayableCard card) {
        this.card = card;
    }

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateResourceCard2(card);
        return false;
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateResourceCard2(card);
        } catch (RemoteException ignored) {
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
