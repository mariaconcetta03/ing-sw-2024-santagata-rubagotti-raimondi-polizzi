package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Game;
import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This event is useful to communicate that the game state has changed
 */
public class UpdateGameStateEvent implements Event{
        private Game.GameState gameState;
        private Boolean startCheckConnection; //this has to be initialized when the Event is instantiated

        public UpdateGameStateEvent(Game.GameState gameState, boolean theGameHasJustStarted) {

            this.gameState = gameState;
            this.startCheckConnection = theGameHasJustStarted;
        }

        @Override
        public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
            client.updateGameState(gameState);
            // this method starts the schedulerToSendHeartbeat (client-side)
            if(gameState.equals(Game.GameState.STARTED)){
                client.startHeartbeat(); // the client starts to check the server heartbeat
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // I have to do the shutdown when the game is terminated
                scheduler.scheduleAtFixedRate(() -> { // equivalent to the schedulerToSendHeartbeat (but in the server-side)
                    if (!wrappedObserver.getADisconnectionHappened()){
                        try {
                            client.heartbeat();
                        } catch (RemoteException e) {
                            wrappedObserver.setADisconnectionHappened(true);
                        }
                    }else{
                        scheduler.shutdown();
                    }
                }, 0, wrappedObserver.getHeartbeatInterval(), TimeUnit.SECONDS);
                wrappedObserver.setScheduler(scheduler);
            }
            return false;

        }
        @Override
        public void executeSCK(ClientSCK client) {

                client.updateGameState(gameState);

        }

    @Override
        public boolean executeSCKServerSide() {
            return this.startCheckConnection;

        }

}
