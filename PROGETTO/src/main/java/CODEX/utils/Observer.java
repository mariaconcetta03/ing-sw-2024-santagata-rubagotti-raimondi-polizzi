package CODEX.utils;

import CODEX.utils.executableMessages.events.Event;

/**
 * This class represents a listener to be added to the specific Observable.
 */
public interface Observer {
    String nickname = null;

    /**
     * This is an update method
     *
     * @param obs observable
     * @param e   event of update
     */
    void update(Observable obs, Event e);


    /**
     * Setter method
     *
     * @param nickname chosen by the player
     */
    void setNickname(String nickname);


    /**
     * Setter method
     *
     * @param aDisconnectionHappened true in a disconnection happened, false otherwise
     */
    void setADisconnectionHappened(boolean aDisconnectionHappened);
}

