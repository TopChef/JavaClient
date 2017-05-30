package ca.uwaterloo.iqc.topchef.adapters.java.net;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.Optional;

/**
 * Describes the contract for a URL connection
 */
public interface URLConnection {

    /**
     * @return The request method that this connection is making
     */
    HTTPRequestMethod getRequestMethod();

    /**
     *
     * @param method The desired HTTP method to set
     * @throws ProtocolException if the underlying setter throws a {@link ProtocolException} as well
     */
    void setRequestMethod(HTTPRequestMethod method) throws ProtocolException;

    /**
     *
     * @return The response code for the request
     * @throws IOException If the response code could not be obtained
     */
    HTTPResponseCode getResponseCode() throws IOException;

    /**
     * Return the value of the header. Since this value might not be defined,
     * it is wrapped conveniently in an {@link Optional
     *
     * @param key The key of the property
     * @return The value to which the desired attribute is set
     *
     */
    Optional<String> getRequestProperty(String key);

    /**
     * Establish the connection to another server
     *
     * @throws IllegalStateException If the connection is already connected.
     * @throws IOException If the connection cannot be completed
     */
    void connect() throws IllegalStateException, IOException;

    /**
     * Close the connection
     */
    void disconnect();
}
