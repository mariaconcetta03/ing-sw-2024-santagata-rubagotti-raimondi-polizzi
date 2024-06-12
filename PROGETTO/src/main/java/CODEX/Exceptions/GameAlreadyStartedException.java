package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

/**
 * This exception is thrown when a game is already started.
 */
public class GameAlreadyStartedException extends Exception implements ExceptionAssociatedWithAnEvent {
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public GameAlreadyStartedException (String errormessage){
        super(errormessage);
    }

    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.GAME_ALREADY_STARTED;
    }
}
