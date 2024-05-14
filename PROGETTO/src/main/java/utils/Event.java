package utils;

import java.io.Serializable;
import java.util.*;



/**
 * This enum represents an EVENT, which is the answer that the server gives to
 * the client, after a taken action.
 */
public enum Event implements Serializable {
    // ERRORS
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

    // UPDATES
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
    UPDATED_ROUND,
    GAME_STATE_CHANGED,
    SETUP_PHASE_1,
    SETUP_PHASE_2,
    NEW_TURN,

    //some new events to be used in SCKMessage to tell the server what the client want to do
    ALL_CONNECTED, //è un per avere un esempio (possiamo modificarlo)
    START, //per dire che il client TCP ha caricato localmente una copia dell'intero model

    ASK_SERVER_MODEL,  //per inizializzare il model del client usando quello del server
    GAME_BOARD,

    GAME_PLAYERS,

    //SELECTIONS
    NICKNAME_SELECTION,

    //each one of these events has a corresponding function in ClientGeneralInterface (to be used in ClientSCK)
    ADD_PLAYER_TO_LOBBY,
    CHOOSE_NICKNAME,
    CREATE_LOBBY,
    PLAY_CARD,
    PLAY_BASE_CARD,
    DRAW_CARD,
    CHOOSE_OBJECTIVE_CARD,
    CHOOSE_PAWN_COLOR,
    SEND_MESSAGE,
    LEAVE_GAME;

    //end of events with a corresponding function in ClientGeneralInterface
    public void printEvent(){
        System.out.println("Questo è l'evento: " + this.toString());
    }


}
