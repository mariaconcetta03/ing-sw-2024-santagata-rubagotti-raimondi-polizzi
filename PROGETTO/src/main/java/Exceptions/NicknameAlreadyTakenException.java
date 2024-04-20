package Exceptions;

public class NicknameAlreadyTakenException extends Exception{
    /**
     * Constructor with the specified error message
     * @param errormessage is the message we want to give
     */
    public NicknameAlreadyTakenException(String errormessage){
        super(errormessage);
    }
}
