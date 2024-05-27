package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.PlayableDeck;

import java.rmi.RemoteException;

public class updateGoldDeckEvent implements Event {
    /**
     * Constructor method
     * @param goldDeck is the card to be updated.
     */
    public updateGoldDeckEvent(PlayableDeck goldDeck){
        this.goldDeck=goldDeck;
    }
    private PlayableDeck goldDeck;

    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateGoldDeck(goldDeck);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateGoldDeck(goldDeck);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
