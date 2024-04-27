package Exceptions;

public class GameAlreadyStartedException extends Exception {
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public GameAlreadyStartedException (String errormessage){
        super(errormessage);
    }
}
