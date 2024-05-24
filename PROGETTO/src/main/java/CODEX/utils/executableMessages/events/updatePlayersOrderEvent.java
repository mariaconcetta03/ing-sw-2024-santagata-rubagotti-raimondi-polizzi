package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.Player;

import java.rmi.RemoteException;
import java.util.List;

public class updatePlayersOrderEvent implements Event { //questo era prima l'evento UPDATED_ROUND
    private List<Player> newPlayingOrder;

    public updatePlayersOrderEvent(List<Player> newPlayingOrder) {
        this.newPlayingOrder = newPlayingOrder;
    }
    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.updateRound(newPlayingOrder);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateRound(newPlayingOrder);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
