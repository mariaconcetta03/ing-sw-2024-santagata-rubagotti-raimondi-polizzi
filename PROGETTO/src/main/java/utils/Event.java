package utils;

import java.io.Serializable;
import java.util.*;



/**
 * This enum represents an EVENT, which is the answer that the server gives to
 * the client, after a taken action.
 */
public enum Event implements Serializable {
    OK,
    CARD_NOT_DRAWN,
    GAME_NOT_EXISTS,
    GAME_ALREADY_STARTED,
    CARD_NOT_PLAYED,
    NOT_RECEIVER,
    OBJECTIVE_CARD_NOT_OWNED,
    FULL_LOBBY,
    NICKNAME_ALREADY_TAKEN,
    INVALID_GAME_STATUS,
    UNABLE_TO_PLAY_CARD,
    NOT_YOUR_TURN;

    public void printEvent(){
        System.out.println("Questo è l'evento: " + this.toString());
    }

}
