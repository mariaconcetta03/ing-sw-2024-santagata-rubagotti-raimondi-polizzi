package Exceptions;

public class CardNotOwnedException extends Exception{
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public CardNotOwnedException(String errormessage){
        super(errormessage);
    }
}
