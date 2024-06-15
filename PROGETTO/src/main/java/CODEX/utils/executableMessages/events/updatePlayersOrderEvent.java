package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Player;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This event is useful to communicate that the playing order has changed
 */
public class updatePlayersOrderEvent implements Event {
    private List<Player> newPlayingOrder;
    static int id=-1;

    public updatePlayersOrderEvent(List<Player> newPlayingOrder) {
        this.newPlayingOrder = newPlayingOrder;
    }
    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        id++;
        System.out.println("-----------------ENTRATO NELLA EXECUTE-------  "+id+"------------");
        System.out.println("sto per fare l'updateround "+id);
        client.updateRound(newPlayingOrder);
        System.out.println("ho fatto l'updateround   "  +id);
        return false;
    }
    @Override
    public void executeSCK(ClientSCK client) {

            client.updateRound(newPlayingOrder);

    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
