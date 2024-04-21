package utils;

import java.io.Serializable;
import java.util.*;


/**
 * This class represents an EVENT, which is the answer that the server gives to
 * the client, after a taken action.
 */
public class Event implements Serializable {

    EventType event;

    public enum EventType {
        GOOD,
        BAD,
        ADDED_TO_LOBBY,
        BASE_CARD_PLAYED,
        BELONGING_GAME_ID,
        BOARD_SET,
        CARD_DRAWN,
        CARD_NOT_DRAWN,
        CARD_NOT_PLAYED,
        CARD_PLAYED,
        CHAT_CREATED,
        COLOR_SET,
        FULL_LOBBY,
        GAME_CREATED,
        GAME_ENDED,
        GAME_SET,
        GAME_STARTED,
        GENERAL_CHAT_CREATED,
        ISFIRST_SET,
        LOBBY_CREATED,
        MESSAGE_SENT,
        NEW_ROUND,
        NICKNAME_ALREADY_TAKEN,
        NICKNAME_SET,
        NOT_YOUR_TURN,
        NUMBER_OF_PLAYERS_SET,
        NUM_OBJECTIVES_REACHED_ADDED,
        OBJECTIVE_CARD_CHOSEN,
        PERSONAL_OBJECTIVE_ADDED,
        PLAYER_ADDED,
        POINTS_ADDED
    }


    /**
     * Class constructor
     * @param event is the type of the event which the server wants to return
     */
    public Event (EventType event){
        this.event = event;
    }


    /**
     * This method prints out the type of event which has been returned by the server
     * to the client.
     */
    public void printEvent() {
        System.out.println ("Questo è l'evento " + this.event.toString());
    }

}
