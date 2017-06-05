package ca.uwaterloo.iqc.topchef.adapters.java.net;

import com.github.dmstocking.optional.java.util.Optional;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;

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
     * it is wrapped conveniently in an {@link Optional}
     *
     * @param key The key of the property
     * @return The value to which the desired attribute is set
     *
     */
    Optional<String> getRequestProperty(String key);

    void setRequestProperty(@NotNull String key, String value);

    /**
     *
     * @return A stream that can be used to read the request body
     * @throws IOException If the stream cannot be retrieved
     */
    InputStream getInputStream() throws IOException;

    /**
     *
     * @return A stream that can be used to write to the request body
     * @throws IOException If the stream cannot be retrieved
     */
    OutputStream getOutputStream() throws IOException;

    /**
     *
     * @return Whether output is allowed on this connection.
     */
    Boolean getDoOutput();

    /**
     *
     * @param doOutput Whether output to this connection should be allowed or not.
     */
    void setDoOutput(Boolean doOutput);

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
