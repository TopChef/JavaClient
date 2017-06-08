package ca.uwaterloo.iqc.topchef.exceptions;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;

/**
 * Thrown if a resource cannot be found
 */
public class ResourceNotFoundException extends HTTPException {
    public ResourceNotFoundException(){
        super(HTTPResponseCode.NOT_FOUND);
    }

    public ResourceNotFoundException(String message){
        super(message);
    }

    public ResourceNotFoundException(Throwable error){
        super(HTTPResponseCode.NOT_FOUND, error);
    }
}
