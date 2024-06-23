package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

/**
 * This exception is thrown when a lobby is full and a player can't enjoy it.
 */
public class FullLobbyException extends Exception implements ExceptionAssociatedWithAnEvent {
    /**
     * Class constructor
     *
     * @param errormessage is the message we want to give
     */
    public FullLobbyException(String errormessage) {
        super(errormessage);
    }

    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.FULL_LOBBY;
    }
}
