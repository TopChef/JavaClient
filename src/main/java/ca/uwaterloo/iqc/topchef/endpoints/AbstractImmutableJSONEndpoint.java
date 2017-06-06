package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.exceptions.*;
import org.jetbrains.annotations.Contract;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
     * A parser used to parse JSON
     */
    private static final JSONParser parser = new JSONParser();

    /**
     *
     * @param url The URL at which this endpoint is located
     */
    public AbstractImmutableJSONEndpoint(URL url){
        super(url);
    }

    /**
     *
     * @return A {@link JSONObject} containing the request body. If the request returned a JSON array (very unlikely),
     * then the array will be located within this request in the "data" keyword
     * @throws ParseException If the returned string could not be parsed
     * @throws IOException If there is a problem talking to the API
     * @throws HTTPException If an HTTP status code other than {@link HTTPResponseCode#OK} was returned with this
     * get Request
     */
    @Override
    public JSONObject getJSON() throws ParseException, IOException, HTTPException {
        URLConnection connection = openConnection(this.getURL());
        configureConnectionForJSONGet(connection);

        connection.connect();

        try {
            assertGoodResponseCode(connection);
        } catch (HTTPException error){
            handleBadConnectionAssert(error, connection);
            throw error;
        }

        try {
            return readJSONFromConnection(connection);
        } catch (ClassCastException error) {
            handleJSONObjectCastException(error);
            throw new IOException(error);
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Check that the response code is correct
     *
     * @param connection The connection whose HTTP status code is to be checked
     * @throws MethodNotAllowedException If the HTTP status code is 405
     * @throws ResourceNotFoundException If the HTTP response code is 404
     * @throws InternalServerErrorException For HTTP status code 500
     * @throws NoContentException For HTTP status code 204
     * @throws IOException If the HTTP status code could not be retrieved
     */
    private void assertGoodResponseCode(URLConnection connection) throws MethodNotAllowedException,
            ResourceNotFoundException, InternalServerErrorException, NoContentException, IOException {
        HTTPResponseCode code = connection.getResponseCode();

        switch (code) {
            case NOT_FOUND:
                throw new ResourceNotFoundException();
            case METHOD_NOT_ALLOWED:
                throw new MethodNotAllowedException();
            case INTERNAL_SERVER_ERROR:
                throw new InternalServerErrorException();
            case NO_CONTENT:
                throw new NoContentException();
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
     * @param connection The connection from which data is to be read
     * @return The parsed JSON data
     * @throws ParseException If the returned data cannot be parsed
     * @throws IOException If communication with the API cannot be established
     * @throws ClassCastException If the resulting data cannot be cast to a {@link JSONObject}
     */
    private static JSONObject readJSONFromConnection(URLConnection connection) throws ParseException, IOException,
            ClassCastException {
        InputStream stream = connection.getInputStream();
        Reader reader = new InputStreamReader(stream);

        Object result = parser.parse(reader);

        return castToJSONObject(result);
    }

    /**
     *
     * @param result The result from the JSON parser
     * @return The result cast to an {@link JSONObject}
     * @throws ClassCastException If the result cannot be cast correctly
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    private static JSONObject castToJSONObject(Object result) throws ClassCastException {
        JSONObject jsonData;
        if (result instanceof JSONArray) {
            jsonData = new JSONObject();
            jsonData.put("data", result);
        } else {
            jsonData = (JSONObject) result;
        }
        return jsonData;
    }

    /**
     *
     * @param error The error to handle
     */
    private static void handleConnectionCastException(HTTPConnectionCastException error) {
        log.error("Attempting to cast to HTTP connection threw error", error);
    }

    /**
     *
     * @param error The handled error
     */
    private static void handleJSONObjectCastException(ClassCastException error){
        log.error("Attempting to cast object to JSONObject threw error", error);
    }

    /**
     *
     * @param error The error to handle
     * @param offendingConnection The connection that caused the error
     */
    private static void handleBadConnectionAssert(Exception error, URLConnection offendingConnection){
        offendingConnection.disconnect();
        log.error("Attempting to get JSON resulted in invalid response code", error);
    }
}
