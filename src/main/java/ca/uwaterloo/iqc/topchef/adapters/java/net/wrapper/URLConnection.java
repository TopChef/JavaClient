package ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

/**
 * Wraps {@link java.net.HttpURLConnection} as a URL connection. I doubt we'll be connecting to other jars via
 * URL anytime soon.
 */
public final class URLConnection implements ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection {
    private HttpURLConnection connection;

    public URLConnection(HttpURLConnection connection){
        this.connection = connection;
    }

    @Override
    public HTTPRequestMethod getRequestMethod(){
        String method = this.connection.getRequestMethod().toUpperCase();

        switch (method) {
            case "GET":
                return HTTPRequestMethod.GET;
            case "POST":
                return HTTPRequestMethod.POST;
            case "PATCH":
                return HTTPRequestMethod.PATCH;
            case "PUT":
                return HTTPRequestMethod.PUT;
            default:
                throw new RuntimeException(
                        String.format("Method %s not defined", method)
                );
        }
    }

    @Override
    public void setRequestMethod(HTTPRequestMethod method) throws ProtocolException {
        switch (method) {
            case GET:
                this.connection.setRequestMethod("GET");
                break;
            case POST:
                this.connection.setRequestMethod("POST");
                break;
            case PATCH:
                this.connection.setRequestMethod("PATCH");
                break;
            case PUT:
                this.connection.setRequestMethod("PUT");
                break;
            default:
                throw new ProtocolException("Method not recognized");
        }
    }

    @Override
    public HTTPResponseCode getResponseCode() throws IOException {
        Integer response = this.connection.getResponseCode();

        switch (response) {
            case HttpURLConnection.HTTP_OK:
                return HTTPResponseCode.OK;
            case HttpURLConnection.HTTP_CREATED:
                return HTTPResponseCode.CREATED;
            case HttpURLConnection.HTTP_ACCEPTED:
                return HTTPResponseCode.ACCEPTED;
            case HttpURLConnection.HTTP_NO_CONTENT:
                return HTTPResponseCode.NO_CONTENT;
            case HttpURLConnection.HTTP_BAD_REQUEST:
                return HTTPResponseCode.BAD_REQUEST;
            case HttpURLConnection.HTTP_NOT_FOUND:
                return HTTPResponseCode.NOT_FOUND;
            case HttpURLConnection.HTTP_BAD_METHOD:
                return HTTPResponseCode.METHOD_NOT_ALLOWED;
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                return HTTPResponseCode.INTERNAL_SERVER_ERROR;
            default:
                return HTTPResponseCode.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public void connect() throws IllegalStateException, IOException {
        this.connection.connect();
    }

    @Override
    public void disconnect(){
        this.connection.disconnect();
    }
}
