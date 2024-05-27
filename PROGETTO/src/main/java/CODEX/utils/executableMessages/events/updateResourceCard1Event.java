package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;

public class updateResourceCard1Event implements Event {
    private PlayableCard card;

    public updateResourceCard1Event(PlayableCard card) {
        this.card = card;
    }

    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateResourceCard1(card);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateResourceCard1(card);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
