package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * This event is useful to communicate the winners of the game (and the ranking)
 */
public class WinnerEvent implements Event {
    private Map<Integer, List<String>> finalScoreBoard;
    public WinnerEvent(Map<Integer, List<String>> finalScoreBoard){
        this.finalScoreBoard=finalScoreBoard;
    }
    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.showWinner(finalScoreBoard);
        return false;
    }

    @Override
    public void executeSCK(ClientSCK client) {
            client.showWinner(finalScoreBoard);
    }

    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
