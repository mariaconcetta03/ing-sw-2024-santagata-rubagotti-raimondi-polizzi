package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;


/**
 * This event is useful to communicate that gold card 2 has changed
 */
public class updateGoldCard2Event implements Event {

    public updateGoldCard2Event(PlayableCard card) {
        this.card = card;
    }

    private PlayableCard card;

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateGoldCard2(card);
        return false;
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateGoldCard2(card);
        } catch (RemoteException ignored) {
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
