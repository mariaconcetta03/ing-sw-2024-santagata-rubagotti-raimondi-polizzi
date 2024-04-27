package Exceptions;

public class DeckIsFinishedException extends Exception {

    /**
    * Constructor with the specified error message
    * @param errormessage is the message we want to give
    */
    public DeckIsFinishedException(String errormessage){
        super(errormessage);
    }
}

