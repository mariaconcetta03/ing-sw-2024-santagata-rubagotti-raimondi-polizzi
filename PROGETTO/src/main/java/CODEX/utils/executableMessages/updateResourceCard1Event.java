package CODEX.utils.executableMessages;

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
}
