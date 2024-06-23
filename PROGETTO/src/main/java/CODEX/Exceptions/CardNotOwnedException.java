package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

/**
 * This class sends a message when a card isn't owned by a player
 */
public class CardNotOwnedException extends Exception implements ExceptionAssociatedWithAnEvent {
    /**
     * Class constructor
     *
     * @param errormessage is the message we want to give
     */
    public CardNotOwnedException(String errormessage) {
        super(errormessage);
    }


    /**
     * Getter method
     *
     * @return associatedEvent
     */
    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.OBJECTIVE_CARD_NOT_OWNED;
    }
}
