package CODEX.distributed.RMI;


import CODEX.distributed.ClientGeneralInterface;
import CODEX.utils.Observable;
import CODEX.utils.Observer;
import CODEX.utils.executableMessages.events.Event;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * This class represents a CLIENT as an OBSERVER
 */
public class WrappedObserver implements Observer {
    private final Timer timer;
    public ScheduledExecutorService scheduler;
    private static final int HEARTBEAT_INTERVAL = 2; // seconds
    private ClientGeneralInterface remoteClient;
    private String nickname;
    private boolean aDisconnectionHappened=false;
    private ExecutorService executor;
    private ConcurrentLinkedQueue<Event> eventQueue=new ConcurrentLinkedQueue<>();

    private final LinkedList<Event> queue=new LinkedList<>();
    synchronized public void push(Event item) {
        queue.addFirst(item);
    }
    synchronized public Event pull() {
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

        this.timer = new Timer(true);
        WrappedObserver wrappedObserver=this;
        executor= Executors.newSingleThreadExecutor();
        executor.execute(()-> {
            while (!aDisconnectionHappened&&!Thread.currentThread().isInterrupted()) {
                Event event = null;
                boolean lastEvent = false;
                while (!lastEvent||event!=null) {
                    event = eventQueue.poll();
                    if (event != null) {
                        try {
                            lastEvent = event.execute(remoteClient, wrappedObserver);
                            System.out.println("ho fatto la pull dell'evento");

                        } catch (RemoteException e) {
                            aDisconnectionHappened = true; //bisogna notificarlo al server
                            //gameController.disconnect(); //lo rileva l'heartbeat
                            //timer.cancel();
                        }
                    }

                }
            }
        });
    }




    /**
     * This is an update method
     * @param obs is the observable who called the notify
     */
    public void update(Observable obs, Event event) throws RemoteException {
        System.out.println("ho fatto la push dell'evento");
        eventQueue.add(event);
       // push(e);


    /*
        e.execute(remoteClient,this);

    */


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