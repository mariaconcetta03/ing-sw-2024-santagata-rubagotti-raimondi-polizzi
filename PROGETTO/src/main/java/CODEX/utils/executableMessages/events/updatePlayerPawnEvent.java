package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.Pawn;
import CODEX.org.model.Player;

import java.rmi.RemoteException;

public class updatePlayerPawnEvent implements Event {
    private String nickname;
    private Pawn pawn;

    public updatePlayerPawnEvent(String nickname, Pawn pawn) {
        this.nickname = nickname;
        this.pawn = pawn;
    }

    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updatePawns(nickname, pawn);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updatePawns(nickname, pawn);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
