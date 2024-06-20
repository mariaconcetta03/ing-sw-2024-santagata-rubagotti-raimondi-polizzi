package CODEX.utils;

import CODEX.utils.executableMessages.events.Event;
import java.rmi.RemoteException;

/**
 * this object represents a player to put into the object Observable with maybe other player to define a group of
 * player that can access a single instance of Observable example of use: a group of players takes part to a single chat
 * another example: the player can see the other player decisions ('Observable') during the game session
 */
public interface Observer {
    String nickname = null;

    /**
     * This is an update method
     * @param obs observable
     * @param e event of update
     */
    void update(Observable obs, Event e);


    /**
     * Setter method
     * @param nickname chosen by the player
     */
    public void setNickname(String nickname);


    /**
     * Setter method
     * @param aDisconnectionHappened true in a disconnection happened, false otherwise
     */
    void setADisconnectionHappened(boolean aDisconnectionHappened);


    void setShowWinnerEvent(boolean b);
}

