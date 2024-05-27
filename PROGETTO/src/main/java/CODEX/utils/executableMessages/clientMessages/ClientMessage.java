package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.org.model.Game;
import CODEX.utils.executableMessages.events.Event;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface ClientMessage extends Serializable {
    void execute(ClientHandlerThread clientHandlerThread);

    class updateGameStateEvent implements Event {
        private Game.GameState gameState;
        private Boolean startCheckConnection; //this has to be initialized when the Event is instantiated

        public updateGameStateEvent(Game.GameState gameState) { //aggiungiamo parametro Boolean theGameHasJustStarted

            this.gameState = gameState;
            //this.startCheckConnection = theGameHasJustStarted;
        }

        @Override
        public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
            client.updateGameState(gameState);
        }
        @Override
        public void executeSCK(ClientGeneralInterface client) {
            try {
                client.updateGameState(gameState);
            } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
            }
        }

        @Override
        public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
            return this.startCheckConnection;

        }
    }
}
