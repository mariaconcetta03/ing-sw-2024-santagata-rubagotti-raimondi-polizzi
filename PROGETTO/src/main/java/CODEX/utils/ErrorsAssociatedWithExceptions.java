package CODEX.utils;

import java.io.Serializable;


/**
 * This enum represents an EVENT, which is the answer that the server gives to
 * the client, after a taken action.
 */
public enum ErrorsAssociatedWithExceptions implements Serializable {
    OK,
    COLOR_ALREADY_TAKEN,
    CARD_NOT_DRAWN,
    GAME_NOT_EXISTS,
    GAME_ALREADY_STARTED,
    OBJECTIVE_CARD_NOT_OWNED,
    FULL_LOBBY,
    NICKNAME_ALREADY_TAKEN,
    INVALID_GAME_STATUS,
    UNABLE_TO_PLAY_CARD,
    NOT_YOUR_TURN,
    SETUP_PHASE_2;
}
