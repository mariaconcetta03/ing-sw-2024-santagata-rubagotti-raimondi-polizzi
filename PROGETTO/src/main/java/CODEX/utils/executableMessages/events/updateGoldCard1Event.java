package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.PlayableCard;
import java.rmi.RemoteException;

/**
 * This event is useful to communicate that gold card 1 has changed
 */
public class updateGoldCard1Event implements Event {
    /**
     * Constructor method
     * @param card is the card to be updated.
     */
    public updateGoldCard1Event(PlayableCard card){
        this.card=card;
    }
    private PlayableCard card;

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateGoldCard1(card);
        return false;
    }
    @Override
    public void executeSCK(ClientSCK client) {

            client.updateGoldCard1(card);

    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
