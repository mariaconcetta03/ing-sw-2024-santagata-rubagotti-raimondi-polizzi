package CODEX.Exceptions;

import CODEX.utils.Event;

public class FullLobbyException extends Exception implements ExceptionAssociatedWithAnEvent {
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public FullLobbyException (String errormessage){
        super(errormessage);
    }

    @Override
    public Event getAssociatedEvent() {
        return Event.FULL_LOBBY;
    }
}
