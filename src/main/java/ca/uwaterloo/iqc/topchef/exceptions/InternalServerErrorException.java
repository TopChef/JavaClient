package ca.uwaterloo.iqc.topchef.exceptions;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;

/**
 * Thrown if {@link HTTPResponseCode#INTERNAL_SERVER_ERROR} is thrown somewhere where it shouldn't be
 */
public class InternalServerErrorException extends HTTPException {
    public InternalServerErrorException(){
        super(HTTPResponseCode.INTERNAL_SERVER_ERROR);
    }
}
