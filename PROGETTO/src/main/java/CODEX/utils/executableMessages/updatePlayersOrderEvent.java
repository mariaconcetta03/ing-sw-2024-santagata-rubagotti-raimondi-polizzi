package CODEX.utils.executableMessages;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.Player;

import java.rmi.RemoteException;
import java.util.List;

public class updatePlayersOrderEvent implements Event{
    private List<Player> newPlayingOrder;

    public updatePlayersOrderEvent(List<Player> newPlayingOrder) {
        this.newPlayingOrder = newPlayingOrder;
    }
    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.updateRound(newPlayingOrder);
    }
}
