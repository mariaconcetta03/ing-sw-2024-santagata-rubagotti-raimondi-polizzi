package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

/**
 * This exception is used when a color has already been chosen
 */
public class ColorAlreadyTakenException extends Exception implements ExceptionAssociatedWithAnEvent {
    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.COLOR_ALREADY_TAKEN;
    }
}
