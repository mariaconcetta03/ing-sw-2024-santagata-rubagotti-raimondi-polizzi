package CODEX.utils.executableMessages;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;
//Inet4Address.getLocalHost().getHostAddress() @TODO da inserire nel Server
public class updateResourceCard2Event implements Event{
    private PlayableCard card;

    public updateResourceCard2Event(PlayableCard card) {
        this.card = card;
    }

    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.updateResourceCard2(card);
    }
}
