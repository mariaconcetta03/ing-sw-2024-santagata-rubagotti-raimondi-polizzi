package CODEX.distributed.RMI;


import CODEX.controller.GameController;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.utils.Observable;
import CODEX.utils.Observer;
import CODEX.utils.executableMessages.events.Event;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * This class represents an observer that contains a reference to a remote Client.
 * Observable notifies a model modification to each observer registered to it.
 * After a notify method is invoked, the update method in this class is then called.
 * The update method push the specific update event into a queue and a thread pulls the events to be executed
 * remotely on the RMI client.
 */
public class WrappedObserver implements Observer {
    private GameController gameController;
    private ScheduledExecutorService scheduler;
    private static final int HEARTBEAT_INTERVAL = 2; // seconds
    private ClientGeneralInterface remoteClient;
    private String nickname;
    private boolean aDisconnectionHappened = false;
    private ExecutorService executor;
    private ConcurrentLinkedQueue<Event> eventQueue = new ConcurrentLinkedQueue<>();


    /**
     * Class constructor
     *
     * @param ro             the RMIClient which is an observer
     * @param gameController is the associated GameController
     */
    public WrappedObserver(ClientGeneralInterface ro, GameController gameController) {

        this.gameController = gameController;
        this.remoteClient = ro;

        WrappedObserver wrappedObserver = this;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Event event;
            boolean lastEvent;
            while (!aDisconnectionHappened && !Thread.currentThread().isInterrupted()) {
                event = null;
                lastEvent = false;
                while ((!lastEvent || event != null) && !aDisconnectionHappened) {
                    event = eventQueue.poll();
                    if (event != null) {
                        try {
                            lastEvent = event.execute(remoteClient, wrappedObserver);
                        } catch (RemoteException e) {
                            aDisconnectionHappened = true;
                            gameController.disconnection();
                        }
                    }

                }
            }
        });
    }


    /**
     * This is an update method
     *
     * @param obs is the observable who called the notify
     */
    public void update(Observable obs, Event event) {
        eventQueue.add(event); //push
    }


    /**
     * Getter method
     *
     * @return HEARTBEAT_INTERVAL is the frequency of heartbeat
     */
    public int getHeartbeatInterval() {
        return this.HEARTBEAT_INTERVAL;
    }


    /**
     * Getter method
     *
     * @return aDisconnectionHappened true if there's been a disconnection, false otherwise
     */
    public boolean getADisconnectionHappened() {
        return aDisconnectionHappened;
    }


    /**
     * Setter method
     *
     * @param scheduledExecutorService sends heartbeats to the client
     */
    public void setScheduler(ScheduledExecutorService scheduledExecutorService) {
        this.scheduler = scheduledExecutorService;
    }


    /**
     * Setter method
     *
     * @param nickname of the player
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    /**
     * Setter method
     *
     * @param aDisconnectionHappened if there's been a disconnection true, false otherwise
     */
    public void setADisconnectionHappened(boolean aDisconnectionHappened) {
        this.aDisconnectionHappened = aDisconnectionHappened;
    }
}