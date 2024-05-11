package Exceptions;

import utils.Event;

public class GameNotExistsException extends Exception implements ExceptionAssociatedWithAnEvent{
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public GameNotExistsException(String errormessage){
        super(errormessage);
    }

    @Override
    public Event getAssociatedEvent() {
        return Event.GAME_NOT_EXISTS;
    }
}
