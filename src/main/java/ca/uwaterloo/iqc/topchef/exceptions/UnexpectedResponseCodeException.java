package ca.uwaterloo.iqc.topchef.exceptions;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;

/**
 * Thrown if an HTTP request to the TopChef API results in an unexpected response code.
 */
public final class UnexpectedResponseCodeException extends HTTPException {

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

    public UnexpectedResponseCodeException(HTTPResponseCode badCode, URLConnection badConnection){
        this(
                String.format(
                        "Connection %s returned bad response code %s",
                        badConnection, badCode
                )
        );
    }
}
