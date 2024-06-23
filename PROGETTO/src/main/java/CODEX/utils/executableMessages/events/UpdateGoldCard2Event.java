package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;


/**
 * This event is useful to communicate that gold card 2 has changed
 */
public class UpdateGoldCard2Event implements Event {

    public UpdateGoldCard2Event(PlayableCard card) {
        this.card = card;
    }

    private PlayableCard card;

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateGoldCard2(card);
        return false;
    }
    @Override
    public void executeSCK(ClientSCK client) {

            client.updateGoldCard2(card);

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;

    }
}
