package Exceptions;

public class GameNotExistsException extends Exception{
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public GameNotExistsException(String errormessage){
        super(errormessage);
    }
}
