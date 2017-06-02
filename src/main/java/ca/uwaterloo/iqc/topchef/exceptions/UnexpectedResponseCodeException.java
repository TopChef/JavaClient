package ca.uwaterloo.iqc.topchef.exceptions;

/**
 * Thrown if an HTTP request to the TopChef API results in an unexpected response code.
 */
public final class UnexpectedResponseCodeException extends Exception {
    /**
     * Default constructor
     */
    public UnexpectedResponseCodeException(){
        super();
    }

    /**
     *
     * @param message The message to throw
     */
    public UnexpectedResponseCodeException(String message){
        super(message);
    }

    /**
     *
     * @param error The error that resulted in this exception being thrown
     */
    public UnexpectedResponseCodeException(Throwable error){
        super(error);
    }
}
