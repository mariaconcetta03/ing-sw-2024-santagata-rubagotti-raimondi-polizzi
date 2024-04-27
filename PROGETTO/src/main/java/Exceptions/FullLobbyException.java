package Exceptions;

public class FullLobbyException extends Exception {
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public FullLobbyException (String errormessage){
        super(errormessage);
    }
}
