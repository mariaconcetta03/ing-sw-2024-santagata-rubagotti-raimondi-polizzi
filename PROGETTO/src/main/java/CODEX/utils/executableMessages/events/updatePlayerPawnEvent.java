package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.Pawn;
import CODEX.org.model.Player;

import java.rmi.RemoteException;

public class updatePlayerPawnEvent implements Event {
    private Player player; //@TODO piu che il player, magari il nickname
    private Pawn pawn;

    public updatePlayerPawnEvent(Player player, Pawn pawn) {
        this.player = player;
        this.pawn = pawn;
    }

    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.updatePawns(player, pawn);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updatePawns(player, pawn);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
