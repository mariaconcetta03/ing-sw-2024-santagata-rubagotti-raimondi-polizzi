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
    NOT_AVAILABLE_PAWN,
    NOT_RECEIVER,
    OBJECTIVE_CARD_NOT_OWNED,
    FULL_LOBBY,
    NICKNAME_ALREADY_TAKEN,
    INVALID_GAME_STATUS,
    UNABLE_TO_PLAY_CARD,
    NOT_YOUR_TURN,
    WRONG_NUMBER_OF_PLAYERS,
    GAME_LEFT,
    UPDATED_BOARD,
    UPDATED_RESOURCE_DECK,
    UPDATED_GOLD_DECK,
    UPDATED_PLAYER_DECK,
    UPDATED_RESOURCE_CARD_1,
    UPDATED_RESOURCE_CARD_2,
    UPDATED_GOLD_CARD_1,
    UPDATED_GOLD_CARD_2,
    UPDATED_CHAT,
    UPDATED_PAWNS,
    UPDATED_NICKNAME,
    UPDATED_ROUND;

    public void printEvent(){
        System.out.println("Questo è l'evento: " + this.toString());
    }

}
