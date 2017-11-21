package ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.exceptions.*;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Base class for an endpoint capable of retrieving JSON in the request body
 */
public abstract class AbstractImmutableJSONEndpoint extends AbstractEndpoint implements ImmutableJSONEndpoint {
    /**
     * The application log to which messages of interest will be written
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractImmutableJSONEndpoint.class);

    /**
     * A mapper that can be used to read and marshall JSON
     */
    @Getter
    @Setter
    private ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core
            .wrapper.ObjectMapper();

    /**
     *
     * @param url The URL at which this endpoint is located
     */
    public AbstractImmutableJSONEndpoint(URL url){
        super(url);
    }

    /**
     *
     * @return An {@link Object} containing the request body. If the request returned a JSON array (very unlikely),
     * then the array will be located within this request in the "data" keyword
     * @throws IOException If there is a problem talking to the API
     * @throws HTTPException If an HTTP status code other than {@link HTTPResponseCode#OK} was returned with this
     * get Request
     */
    @Override
    public Object getJSON() throws IOException, HTTPException {
        return getJSON(Object.class);
    }

    /**
     *
     * @param desiredType The class to which the JSON from the server is to be marshalled
     * @param <T> The type to which the JSON is to be marshalled
     * @return An instance of the required type, built from this endpoint
     * @throws IOException If I/O with the server cannot be established
     * @throws HTTPException If I/O can be established, but the response is not satisfactory.
     */
    @Override
    public <T> T getJSON(Class<T> desiredType) throws IOException, HTTPException {
        @Cleanup URLConnection connection = openConnection(this.getURL());
        configureConnectionForJSONGet(connection);
        connection.connect();
        assertGoodResponseCode(connection);
        return mapper.readValue(connection.getInputStream(), desiredType);
    }

    /**
     * Check that the response code is correct
     *
     * @param connection The connection whose HTTP status code is to be checked
     * @throws BadRequestException If the HTTP status code is 400
     * @throws MethodNotAllowedException If the HTTP status code is 405
     * @throws ResourceNotFoundException If the HTTP response code is 404
     * @throws InternalServerErrorException For HTTP status code 500
     * @throws NoContentException For HTTP status code 204
     * @throws IOException If the HTTP status code could not be retrieved
     * @throws UnexpectedResponseCodeException If an Unexpected Response code is received
     */
    protected static void assertGoodResponseCode(URLConnection connection) throws MethodNotAllowedException,
            ResourceNotFoundException, InternalServerErrorException, NoContentException, IOException,
            UnexpectedResponseCodeException, BadRequestException {
        HTTPResponseCode code = connection.getResponseCode();

        switch (code) {
            case OK:
                break;
            case NOT_FOUND:
                throw new ResourceNotFoundException();
            case METHOD_NOT_ALLOWED:
                throw new MethodNotAllowedException();
            case INTERNAL_SERVER_ERROR:
                throw new InternalServerErrorException();
            case NO_CONTENT:
                throw new NoContentException();
            case BAD_REQUEST:
                throw new BadRequestException();
            default:
                throw new UnexpectedResponseCodeException(code, connection);
        }
    }

    /**
     * @param url The URL to which a connection will be opened
     * @return The open connection
     * @throws IOException If there is an error casting the underlying connection to an HTTP Connection
     */
    private static URLConnection openConnection(URL url) throws IOException {
        try {
            return url.openConnection();
        } catch (HTTPConnectionCastException error) {
            handleConnectionCastException(error);
            throw new IOException(error);
        }
    }

    /**
     * Set up the required parameters in order to ``GET`` JSON from the endpoint using a ``GET`` request
     *
     * @param connection The connection to configure
     * @throws IOException If the connection cannot be configured
     */
    private static void configureConnectionForJSONGet(URLConnection connection) throws IOException {
        connection.setDoOutput(Boolean.FALSE);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod(HTTPRequestMethod.GET);
    }

    /**
     *
     * @param error The error to handle
     */
    private static void handleConnectionCastException(HTTPConnectionCastException error) {
        log.error("Attempting to cast to HTTP connection threw error", error);
    }
}
