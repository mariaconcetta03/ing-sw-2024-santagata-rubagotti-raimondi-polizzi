package CODEX.utils.executableMessages;

import CODEX.distributed.ClientActionsInterface;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;

public class updateResourceCard1Event implements Event{
    private PlayableCard card;

    public updateResourceCard1Event(PlayableCard card) {
        this.card = card;
    }

    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
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
    public boolean executeSCKServerSide(ClientActionsInterface client) { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
