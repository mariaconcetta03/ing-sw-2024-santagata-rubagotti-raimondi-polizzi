package Exceptions;

import utils.Event;

public class CardNotOwnedException extends Exception implements ExceptionAssociatedWithAnEvent{
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public CardNotOwnedException(String errormessage){
        super(errormessage);
    }

    @Override
    public Event getAssociatedEvent() {
        return Event.OBJECTIVE_CARD_NOT_OWNED;
    }
}
