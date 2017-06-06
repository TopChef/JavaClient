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
    private static final Logger log = LoggerFactory.getLogger(AbstractImmutableJSONEndpoint.class);

    private static final JSONParser parser = new JSONParser();

    public AbstractImmutableJSONEndpoint(URL url){
        super(url);
    }

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

    private void assertGoodResponseCode(URLConnection connection) throws MethodNotAllowedException,
            ResourceNotFoundException, InternalServerErrorException, IOException {
        HTTPResponseCode code = connection.getResponseCode();

        switch (code) {
            case NOT_FOUND:
                throw new ResourceNotFoundException();
            case METHOD_NOT_ALLOWED:
                throw new MethodNotAllowedException();
            case INTERNAL_SERVER_ERROR:
                throw new InternalServerErrorException();
        }
    }

    private static URLConnection openConnection(URL url) throws IOException {
        try {
            return url.openConnection();
        } catch (HTTPConnectionCastException error) {
            handleConnectionCastException(error);
            throw new IOException(error);
        }
    }

    private static void configureConnectionForJSONGet(URLConnection connection) throws IOException {
        connection.setDoOutput(Boolean.FALSE);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod(HTTPRequestMethod.GET);
    }

    private static JSONObject readJSONFromConnection(URLConnection connection) throws ParseException, IOException,
            ClassCastException {
        InputStream stream = connection.getInputStream();
        Reader reader = new InputStreamReader(stream);

        Object result = parser.parse(reader);

        return castToJSONObject(result);
    }

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

    private static void handleConnectionCastException(HTTPConnectionCastException error) {
        log.error("Attempting to cast to HTTP connection threw error", error);
    }

    private static void handleJSONObjectCastException(ClassCastException error){
        log.error("Attempting to cast object to JSONObject threw error", error);
    }

    private static void handleBadConnectionAssert(Exception error, URLConnection offendingConnection){
        offendingConnection.disconnect();
        log.error("Attempting to get JSON resulted in invalid response code", error);
    }
}
