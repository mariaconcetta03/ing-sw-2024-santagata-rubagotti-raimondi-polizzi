package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

/**
 * This exception is used when the card is not drawable
 */
public class CardNotDrawableException extends Exception implements ExceptionAssociatedWithAnEvent {
    /**
     * Class constructor
     * @param errormessage is the message we want to give
     */
    public CardNotDrawableException(String errormessage) {
        super(errormessage);
    }

    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.UNABLE_TO_PLAY_CARD;
    }
}
