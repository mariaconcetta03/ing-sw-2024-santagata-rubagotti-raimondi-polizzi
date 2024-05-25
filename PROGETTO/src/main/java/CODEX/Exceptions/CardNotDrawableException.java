package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;

public class CardNotDrawableException extends Exception implements ExceptionAssociatedWithAnEvent{
    public CardNotDrawableException(String errormessage){
        super(errormessage);
    }

    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return ErrorsAssociatedWithExceptions.UNABLE_TO_PLAY_CARD;
    }
}
