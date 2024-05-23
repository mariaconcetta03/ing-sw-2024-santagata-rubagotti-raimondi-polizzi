package CODEX.utils.executableMessages;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.Game;

import java.rmi.RemoteException;

public class updateGameStateEvent implements Event{
    private Game.GameState gameState;

    public updateGameStateEvent(Game.GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.updateGameState(gameState);
    }
}
