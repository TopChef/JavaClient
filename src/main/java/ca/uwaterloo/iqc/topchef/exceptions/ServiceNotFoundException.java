package ca.uwaterloo.iqc.topchef.exceptions;


/**
 * Thrown if a service cannot be found
 */
public class ServiceNotFoundException extends ResourceNotFoundException {

    public ServiceNotFoundException(String message){
        super(message);
    }

    public ServiceNotFoundException(Throwable error){
        super(error);
    }
}
