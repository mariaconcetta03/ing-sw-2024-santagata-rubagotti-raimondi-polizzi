package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.Game;

import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class updateGameStateEvent implements Event{
        private Game.GameState gameState;
        private Boolean startCheckConnection; //this has to be initialized when the Event is instantiated

        public updateGameStateEvent(Game.GameState gameState, boolean theGameHasJustStarted) { //aggiungiamo parametro Boolean theGameHasJustStarted

            this.gameState = gameState;
            this.startCheckConnection = theGameHasJustStarted;
        }

        @Override
        public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
            client.updateGameState(gameState); //this method starts the schedulerToSendHeartbeat (client-side)
            if(gameState.equals(Game.GameState.STARTED)){
                client.startHeartbeat(); // the client starts to check the server heartbeat
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); //bisogna fare lo shutdown quando il gioco termina (con ENDED o con una disconnessione)
                scheduler.scheduleAtFixedRate(() -> { //equivalent to the schedulerToSendHeartbeat (but in the server-side)
                    if (!wrappedObserver.getADisconnectionHappened()){
                        try {
                            client.heartbeat(); //in gameController però la prima volta che viene scritta la variabile lastHeartbeatTime è in startHeartbeat
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Sent heartbeat");
                    }else{
                        scheduler.shutdown();
                    }
                }, 0, wrappedObserver.getHeartbeatInterval(), TimeUnit.SECONDS);
                wrappedObserver.setScheduler(scheduler);
            }
            /*
            if(gameState.equals(Game.GameState.ENDED)){
            wrappedObserver.getScheduler().shutdownNow();
            }

             */
return false;

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
