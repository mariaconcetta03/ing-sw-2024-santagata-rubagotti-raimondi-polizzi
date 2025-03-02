package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.PlayableCard;
import java.rmi.RemoteException;


/**
 * This event is useful to communicate that resource card 2 has changed
 */
public class UpdateResourceCard2Event implements Event {
    private PlayableCard card;

    public UpdateResourceCard2Event(PlayableCard card) {
        this.card = card;
    }

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateResourceCard2(card);
        return false;
    }
    @Override
    public void executeSCK(ClientSCK client) {

            client.updateResourceCard2(card);

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;

    }
}
