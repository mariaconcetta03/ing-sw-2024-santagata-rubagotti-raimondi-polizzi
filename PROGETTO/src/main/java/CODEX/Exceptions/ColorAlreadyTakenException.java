package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

public class ColorAlreadyTakenException extends Exception implements ExceptionAssociatedWithAnEvent{
    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.COLOR_ALREADY_TAKEN;
    }
}
