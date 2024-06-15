package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

/**
 * This exception is used when a color has already been chosen
 */
public class ColorAlreadyTakenException extends Exception implements ExceptionAssociatedWithAnEvent {
    /**
     * Default constructor
     */
    public ColorAlreadyTakenException() {

    }



    /**
     * This method is used to get the associated event
     * @return COLOR_ALREADY_TAKEN
     */
    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.COLOR_ALREADY_TAKEN;
    }
}
