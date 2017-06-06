package ca.uwaterloo.iqc.topchef.exceptions;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;

/**
 * Thrown if an HTTP method is sent to an endpoint where the method is not
 * allowed. This is done when an endpoint gives
 * {@link ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode#METHOD_NOT_ALLOWED}
 */
public class MethodNotAllowedException extends HTTPException {
    public MethodNotAllowedException(){
        super(HTTPResponseCode.METHOD_NOT_ALLOWED);
    }
}
