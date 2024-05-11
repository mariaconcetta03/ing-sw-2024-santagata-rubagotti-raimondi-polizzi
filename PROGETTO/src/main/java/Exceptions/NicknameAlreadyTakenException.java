package Exceptions;

import utils.Event;

public class NicknameAlreadyTakenException extends Exception implements ExceptionAssociatedWithAnEvent{
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public NicknameAlreadyTakenException(String errormessage){
        super(errormessage);
    }

    @Override
    public Event getAssociatedEvent() {
        return Event.NICKNAME_ALREADY_TAKEN;
    }
}
