package CODEX.utils.executableMessages;

import CODEX.distributed.ClientActionsInterface;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;

public class updateGoldCard2Event implements Event {
    /**
     * Constructor method
     *
     * @param card is the card to be updated.
     */
    public updateGoldCard2Event(PlayableCard card) {
        this.card = card;
    }

    private PlayableCard card;

    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.updateGoldCard2(card);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateGoldCard2(card);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide(ClientActionsInterface client) { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
