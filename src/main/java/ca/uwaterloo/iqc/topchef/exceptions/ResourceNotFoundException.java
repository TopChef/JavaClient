package ca.uwaterloo.iqc.topchef.exceptions;

/**
 * Thrown if a resource cannot be found
 */
public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String message){
        super(message);
    }

    public ResourceNotFoundException(Throwable error){
        super(error);
    }
}
