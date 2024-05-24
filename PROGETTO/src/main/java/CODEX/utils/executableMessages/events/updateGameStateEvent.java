package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientActionsInterface;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.Game;

import java.rmi.RemoteException;


public class updateGameStateEvent implements Event{
        private Game.GameState gameState;
        private Boolean startCheckConnection; //this has to be initialized when the Event is instantiated

        public updateGameStateEvent(Game.GameState gameState) { //aggiungiamo parametro Boolean theGameHasJustStarted

            this.gameState = gameState;
            //this.startCheckConnection = theGameHasJustStarted;
        }

        @Override
        public void execute(ClientGeneralInterface client) throws RemoteException {
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
