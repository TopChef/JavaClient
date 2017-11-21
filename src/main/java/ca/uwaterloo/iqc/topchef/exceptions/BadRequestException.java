package ca.uwaterloo.iqc.topchef.exceptions;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;

/**
 * Thrown if an error code of 400 is returned from the server
 */
public class BadRequestException extends HTTPException {
    /**
     * Instantiate an {@link HTTPException} using the status code of {@link HTTPResponseCode#BAD_REQUEST}
     */
    public BadRequestException(){
        super(HTTPResponseCode.BAD_REQUEST);
    }
}
