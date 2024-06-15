package CODEX.utils;

import java.io.Serializable;


/**
 * This enum represents an EVENT, which is the answer that the server gives to
 * the client, after a taken action.
 */
public enum ErrorsAssociatedWithExceptions implements Serializable {
    /**
     * OK
     */
    OK,


    /**
     * color already taken
     */
    COLOR_ALREADY_TAKEN,


    /**
     * card not drawn
     */
    CARD_NOT_DRAWN,


    /**
     * game not exists
     */
    GAME_NOT_EXISTS,


    /**
     * game already started
     */
    GAME_ALREADY_STARTED,


    /**
     * objective card not owned
     */
    OBJECTIVE_CARD_NOT_OWNED,


    /**
     * full lobby
     */
    FULL_LOBBY,


    /**
     * nickname already taken
     */
    NICKNAME_ALREADY_TAKEN,


    /**
     * invalid game status
     */
    INVALID_GAME_STATUS,


    /**
     * unable to play card
     */
    UNABLE_TO_PLAY_CARD,


    /**
     * not your turn
     */
    NOT_YOUR_TURN,


    /**
     * setup phase 2
     */
    SETUP_PHASE_2;
}
