package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

public class CardNotOwnedException extends Exception implements ExceptionAssociatedWithAnEvent{
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public CardNotOwnedException(String errormessage){
        super(errormessage);
    }

    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.OBJECTIVE_CARD_NOT_OWNED;
    }
}
