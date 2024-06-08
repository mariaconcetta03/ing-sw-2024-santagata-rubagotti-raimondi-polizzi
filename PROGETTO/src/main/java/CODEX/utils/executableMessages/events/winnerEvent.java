package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class winnerEvent implements Event {
    Map<Integer, List<String>> finalScoreBoard;
    public winnerEvent(Map<Integer, List<String>> finalScoreBoard){
        this.finalScoreBoard=finalScoreBoard;
    }
    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.showWinner(finalScoreBoard);
        return false;
    }

    @Override
    public void executeSCK(ClientGeneralInterface client) {

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
