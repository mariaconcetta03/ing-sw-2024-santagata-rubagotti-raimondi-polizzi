package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;

import java.rmi.RemoteException;

public class disconnectionEvent implements Event {  //sostituisce quello che prima era l'evento di GAME_LEFT
    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        /*
        wrappedObserver.setADisconnectionHappened(true); //questo mi serve per interrompere lo scheduler passivamente se client.handleDisconnection() lancia un'eccezione
        System.out.println("sono dentro execute di disconnectionEvent");
        client.handleDisconnection(); //interrompo lo scheduler che RICEVE gli heartBeat
        System.out.println("fatto client.handleDisconnection() , ora faccio  wrappedObserver.getScheduler().shutdownNow();");
        wrappedObserver.getScheduler().shutdown(); //interrompo lo scheduler che MANDA gli heartBeat
        client.handleDisconnectionFunction(); //faccio la exit (client-side)

         */
    }

    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.handleDisconnection();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean executeSCKServerSide( ) { //returns true when we are considering updateGameState and the new state is 'STARTED'
       return false;

    }
}
