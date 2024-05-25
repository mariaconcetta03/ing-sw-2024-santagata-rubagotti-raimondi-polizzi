package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;


public class GameNotExistsException extends Exception implements ExceptionAssociatedWithAnEvent{
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public GameNotExistsException(String errormessage){
        super(errormessage);
    }

    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.GAME_NOT_EXISTS;
    }
}
