package CODEX.Exceptions;

import CODEX.utils.Event;

public class GameAlreadyStartedException extends Exception implements ExceptionAssociatedWithAnEvent {
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public GameAlreadyStartedException (String errormessage){
        super(errormessage);
    }

    @Override
    public Event getAssociatedEvent() {
        return Event.GAME_ALREADY_STARTED;
    }
}
