package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.Pawn;
import java.rmi.RemoteException;

/**
 * This event is useful to communicate that player pawn has changed
 */
public class updatePlayerPawnEvent implements Event {
    private String nickname;
    private Pawn pawn;

    public updatePlayerPawnEvent(String nickname, Pawn pawn) {
        this.nickname = nickname;
        this.pawn = pawn;
    }

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updatePawns(nickname, pawn);
        return false;
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updatePawns(nickname, pawn);
        } catch (RemoteException ignored) {
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
