package ca.uwaterloo.iqc.topchef.exceptions;


/**
 * Thrown if {@link ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL}
 * opens a connection, and is unable to cast that connection to an
 * {@link java.net.HttpURLConnection}.
 */
public class HTTPConnectionCastException extends AdapterException {
    public HTTPConnectionCastException(String message){
        super(message);
    }

    public HTTPConnectionCastException(Throwable throwable){
        super(throwable);
    }

}
