package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;
//Inet4Address.getLocalHost().getHostAddress() @TODO da inserire nel Server
public class updateResourceCard2Event implements Event {
    private PlayableCard card;

    public updateResourceCard2Event(PlayableCard card) {
        this.card = card;
    }

    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.updateResourceCard2(card);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateResourceCard2(card);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
