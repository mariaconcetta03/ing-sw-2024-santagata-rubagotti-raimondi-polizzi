package CODEX.utils.executableMessages;

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
}
