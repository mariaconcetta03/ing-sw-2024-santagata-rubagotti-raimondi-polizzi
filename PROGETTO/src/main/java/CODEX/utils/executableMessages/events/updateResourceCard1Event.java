package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;



/**
 * This event is useful to communicate that resource card 1 has changed
 */
public class updateResourceCard1Event implements Event {
    private PlayableCard card;

    public updateResourceCard1Event(PlayableCard card) {
        this.card = card;
    }

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateResourceCard1(card);
        return false;
    }
    @Override
    public void executeSCK(ClientSCK client) {

            client.updateResourceCard1(card);

    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
