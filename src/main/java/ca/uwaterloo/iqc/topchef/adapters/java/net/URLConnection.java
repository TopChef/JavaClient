package ca.uwaterloo.iqc.topchef.adapters.java.net;

import java.io.IOException;
import java.net.ProtocolException;

/**
 * Describes the contract for a URL connection
 */
public interface URLConnection {
    /**
     *
     * @return The request method that this connection is making
     */
    HTTPRequestMethod getRequestMethod();

    /**
     *
     * @param method The desired HTTP method to set
     */
    void setRequestMethod(HTTPRequestMethod method) throws ProtocolException;

    /**
     *
     * @return The response code for the request
     * @throws IOException If the response code could not be obtained
     */
    HTTPResponseCode getResponseCode() throws IOException;

    /**
     * Establish the connection to another server
     *
     * @throws IllegalStateException If the connection is already connected.
     * @throws IOException If the connection cannot be completed
     */
    void connect() throws IllegalStateException, IOException;

    /**
     * Disconnect
     */
    void disconnect();
}
