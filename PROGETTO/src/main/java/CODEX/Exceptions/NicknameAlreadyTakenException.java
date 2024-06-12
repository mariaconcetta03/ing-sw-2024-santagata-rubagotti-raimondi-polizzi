package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

/**
 * This exception is thrown when a nickname is already taken.
 */
public class NicknameAlreadyTakenException extends Exception implements ExceptionAssociatedWithAnEvent{
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public NicknameAlreadyTakenException(String errormessage){
        super(errormessage);
    }



    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.NICKNAME_ALREADY_TAKEN;
    }
}
