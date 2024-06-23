package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Pawn;
import java.rmi.RemoteException;

/**
 * This event is useful to communicate that player pawn has changed
 */
public class UpdatePlayerPawnEvent implements Event {
    private String nickname;
    private Pawn pawn;

    public UpdatePlayerPawnEvent(String nickname, Pawn pawn) {
        this.nickname = nickname;
        this.pawn = pawn;
    }

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updatePawns(nickname, pawn);
        return false;
    }
    @Override
    public void executeSCK(ClientSCK client) {

            client.updatePawns(nickname, pawn);

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;

    }
}
