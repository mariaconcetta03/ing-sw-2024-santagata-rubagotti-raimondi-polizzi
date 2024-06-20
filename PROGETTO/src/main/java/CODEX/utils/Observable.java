package CODEX.utils;

import CODEX.utils.executableMessages.events.Event;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class represents an observable, which is an object which can be modified, and all the modifications
 * are communicated to the listeners (observers)
 */
public class Observable {
    private List<Observer> observers;



    /**
     * Class constructor
     */
    public Observable() {
        this.observers = new ArrayList<>();
    }



    /**
     * This method will be called at the end of the model's methods that "modify" something.
     * It will inform the Observers to perform an update action contacting the Client related to them.
     * @param e has caused the modification
     */
    public void notifyObservers(Event e) {
        for (Observer obs : observers) {
            obs.update(this, e);
        }
    }



    /**
     * This method add an Observer to the Observable
     * @param obs is the Observer we are adding
     */
    public void addObserver(Observer obs) {
        if (!this.observers.contains(obs)) {
            this.observers.add(obs);
        }
    }



    /**
     * This method removes all the observers from the list
     */
    public void removeObservers() {
        for (int i = 0; i < observers.size(); i++) {
            observers.remove(observers.get(i));
        }
    }



    /**
     * This method is used to notify a disconnection event
     */
    public void notifyDisconnectionEvent() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).setADisconnectionHappened(true);
        }
    }


    /**
     * This method is used to notify a showWinner event
     */
    public void notifyWinner() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).setShowWinnerEvent(true);
        }
    }

}
