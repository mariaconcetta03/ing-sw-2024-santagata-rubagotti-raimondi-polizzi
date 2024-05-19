package CODEX.Exceptions;

import CODEX.utils.Event;

public class CardNotDrawableException extends Exception implements ExceptionAssociatedWithAnEvent{
    public CardNotDrawableException(String errormessage){
        super(errormessage);
    }

    @Override
    public Event getAssociatedEvent() {
        return Event.UNABLE_TO_PLAY_CARD;
    }
}
