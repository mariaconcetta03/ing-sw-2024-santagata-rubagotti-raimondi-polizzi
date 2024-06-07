package CODEX.utils;

import java.io.Serializable;


/**
 * This enum represents an EVENT, which is the answer that the server gives to
 * the client, after a taken action.
 */
public enum ErrorsAssociatedWithExceptions implements Serializable {
    // ERRORS
    OK,
    COLOR_ALREADY_TAKEN,
    CARD_NOT_DRAWN,
    GAME_NOT_EXISTS,
    GAME_ALREADY_STARTED,
    CARD_NOT_PLAYED, //sostituito da UNABLE_TO_PLAY_CARD?
    NOT_AVAILABLE_PAWN,
    NOT_RECEIVER, //per la chat?
    OBJECTIVE_CARD_NOT_OWNED,
    FULL_LOBBY,
    NICKNAME_ALREADY_TAKEN,
    INVALID_GAME_STATUS,
    UNABLE_TO_PLAY_CARD,
    NOT_YOUR_TURN,
    WRONG_NUMBER_OF_PLAYERS, //bisogna metterlo nell'eccezione del metodo checkNplayers()


    // UPDATES events removed, these are the ones still used (by public void setLastEvent(Event lastEvent))
    GAME_LEFT, //verrà sostituito da un evento di disconnessione
    SETUP_PHASE_2;

    public void printEvent(){
        System.out.println("Questo è l'evento: " + this.toString());
    }


}
