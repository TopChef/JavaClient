package ca.uwaterloo.iqc.topchef.exceptions;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;

/**
 * Thrown if an HTTP error code is thrown where it shouldn't be
 */
public class HTTPException extends Exception {
    public HTTPException(Throwable error){
        super(error);
    }

    public HTTPException(String message){
        super(message);
    }

    public HTTPException(HTTPResponseCode badCode, Throwable error){
        super(getMessageForResponse(badCode), error);
    }

    public HTTPException(HTTPResponseCode badCode){
        super(getMessageForResponse(badCode));
    }

    private static String getMessageForResponse(HTTPResponseCode badCode){
        return String.format(
                "HTTP request returned unexpected error code %s", badCode.toString()
        );
    }
}
