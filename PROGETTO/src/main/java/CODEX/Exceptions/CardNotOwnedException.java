package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

public class CardNotOwnedException extends Exception implements ExceptionAssociatedWithAnEvent {
    /**
     * Class constructor
     * @param errormessage is the message we want to give
     */
    public CardNotOwnedException(String errormessage) {
        super(errormessage);
    }

    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.OBJECTIVE_CARD_NOT_OWNED;
    }
}
