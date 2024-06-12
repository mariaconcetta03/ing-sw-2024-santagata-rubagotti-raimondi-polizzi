package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

/**
 * This exception is thrown when a deck is finished
 */
public class DeckIsFinishedException extends Exception implements ExceptionAssociatedWithAnEvent {

    /**
     * Class constructor
     * @param errormessage is the message we want to give
     */
    public DeckIsFinishedException(String errormessage) {
        super(errormessage);
    }

    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return null; //qual è l'evento associato?
    }
}

