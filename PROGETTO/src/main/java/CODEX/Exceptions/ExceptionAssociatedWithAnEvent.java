package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

/**
 * This is the interface that associates an exception with an event.
 */
public interface ExceptionAssociatedWithAnEvent {
    public ErrorsAssociatedWithExceptions getAssociatedEvent();
}
