package ca.uwaterloo.iqc.topchef.exceptions;

/**
 * Thrown if an exception occurs in one of the adapter layers of the application
 */
public class AdapterException extends Exception {

    public AdapterException(String message){
        super(message);
    }

    public AdapterException(Throwable throwable){
        super(throwable);
    }
}
