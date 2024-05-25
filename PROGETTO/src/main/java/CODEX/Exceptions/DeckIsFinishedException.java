package CODEX.Exceptions;

import CODEX.utils.ErrorsAssociatedWithExceptions;


public class DeckIsFinishedException extends Exception implements ExceptionAssociatedWithAnEvent {

    /**
    * Constructor with the specified error message
    * @param errormessage is the message we want to give
    */
    public DeckIsFinishedException(String errormessage){
        super(errormessage);
    }

    @Override
    public ErrorsAssociatedWithExceptions getAssociatedEvent() {
        return null; //qual è l'evento associato?
    }
}

