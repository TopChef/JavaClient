package ca.uwaterloo.iqc.topchef.exceptions;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;

/**
 * Thrown if {@link HTTPResponseCode#NO_CONTENT} is thrown when it shouldn't be
 */
public class NoContentException extends HTTPException {
    public NoContentException(){
        super(HTTPResponseCode.NO_CONTENT);
    }
}
