package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;


public class FullLobbyException extends Exception implements ExceptionAssociatedWithAnEvent {
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public FullLobbyException (String errormessage){
        super(errormessage);
    }

    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.FULL_LOBBY;
    }
}
