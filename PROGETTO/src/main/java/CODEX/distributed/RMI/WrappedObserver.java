package CODEX.distributed.RMI;


import CODEX.distributed.ClientGeneralInterface;
import CODEX.utils.Observable;
import CODEX.utils.Observer;
import CODEX.utils.executableMessages.events.Event;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * This class represents a CLIENT as an OBSERVER
 */
public class WrappedObserver implements Observer {
    public ScheduledExecutorService scheduler;
    private static final int HEARTBEAT_INTERVAL = 5; // seconds
    private ClientGeneralInterface remoteClient;
    private String nickname;
    private boolean aDisconnectionHappened=false;
    private ExecutorService executor;
    private final LinkedList<Event> queue=new LinkedList<>();
    public void push(Event item) {
        queue.addFirst(item);
    }
    public Event pull() {
        if (!queue.isEmpty()) {
            return queue.removeLast();
        } else {
            return null;
        }
    }

    // ----------------- C O M E   A V V I E N E   L' U P D A T E ? --------------------
    // OBSERVABLE SI ACCORGE DEL CAMBIAMENTO (CHE PASSA DALLA VIEW, AL CONTROLLER, AL
    // MODEL, CHE è OBSERVABLE). A QUESTO PUNTO, INVOCA LE FUNZIONI DI UPDATE SUI VARI
    // OBSERVERS. QUESTI IN RMI, GRAZIE ALLA CLASSE WRAPPED OBSERVER, VANNO AD INVOCARE
    // DEI METODI DI CLIENTRMI CHE RICEVONO L'OGGETTO AGGIORNATO
    // ---------------------------------------------------------------------------------



    /**
     * Class constructor
     * @param ro the RMIClient
     */
    public WrappedObserver(ClientGeneralInterface ro) {

        remoteClient = ro;

        /*
        executor= Executors.newSingleThreadExecutor();
        executor.execute(()->{
            while (!aDisconnectionHappened) {
                Event event = pull();
                if (event != null) {
                    try {
                        event.execute(remoteClient, this);
                    } catch (RemoteException e) {
                        aDisconnectionHappened=true; //bisogna notificarlo al server
                        //gameController.disconnect();
                    }
                }
            }
        });

         */
    }



    /**
     * This is an update method
     * @param obs is the observable who called the notify
     */
    public void update(Observable obs, Event e) throws RemoteException {
        /*
        push(e);

         */

        e.execute(remoteClient,this);


    }



    /**
     * Setter method
     * @param nickname of the WrappedObserver
     */
    @Override
    public void setNickname(String nickname) throws RemoteException  {
       this.nickname = nickname;
    }



    /**
     * Getter method
     * @return nickname of the WrappedObserver
     */
    @Override
    public String getNickname() throws RemoteException {
        return this.nickname;
    }
    public ScheduledExecutorService getScheduler(){
        return this.scheduler;
    }
    public void setScheduler(ScheduledExecutorService scheduledExecutorService){
        this.scheduler=scheduledExecutorService;
    }
    public int getHeartbeatInterval(){
        return this.HEARTBEAT_INTERVAL;
    }

    public boolean getADisconnectionHappened() {
        return aDisconnectionHappened;
    }

    public void setADisconnectionHappened(boolean aDisconnectionHappened) {
        this.aDisconnectionHappened = aDisconnectionHappened;
    }
}