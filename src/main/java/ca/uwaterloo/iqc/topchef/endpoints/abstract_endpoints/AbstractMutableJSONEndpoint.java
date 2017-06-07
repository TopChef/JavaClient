package ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import ca.uwaterloo.iqc.topchef.exceptions.JSONIsNullException;
import com.github.dmstocking.optional.java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Describes an endpoint to which JSON can be sent
 *
 * @param <T> the type of JSON that will be used to interact with this endpoint
 */
public abstract class AbstractMutableJSONEndpoint<T> extends AbstractEndpoint implements MutableJSONEndpoint<T> {

    /**
     * The log to which messages will be written
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractMutableJSONEndpoint.class);

    /**
     * The mapper used to write JSON to this endpoint
     */
    @Getter
    @Setter
    private ObjectMapper mapper;

    /**
     * The HTTP method used to make the request
     *
     * -- GETTER --
     *
     *  @return The desired request method
     *
     * -- SETTER --
     *
     *  @param requestMethod the method that will be used to make the request
     */
    @Getter
    @Setter
    private HTTPRequestMethod requestMethod;

    /**
     * The JSON that will be used to make the request
     *
     * -- GETTER --
     *
     *  @return The JSON to set, wrapped in a cozy {@link Optional} monad
     */
    @Getter
    private Optional<T> desiredJSON = Optional.empty();

    /**
     *
     * @param url The URL to contact
     */
    public AbstractMutableJSONEndpoint(URL url){
        this(
                url, new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper()
        );
    }

    public AbstractMutableJSONEndpoint(URL url, ObjectMapper mapper){
        super(url);
        this.mapper = mapper;
    }

    /**
     *
     * @param desiredJSON The JSON object that will be written to this request
     */
    @Override
    public void setDesiredJSON(T desiredJSON){
        this.desiredJSON = Optional.of(desiredJSON);
    }

    /**
     *
     * @return {@link Boolean#TRUE} if the request can run and {@link Boolean#FALSE} if not
     */
    @Override
    public Boolean canRun(){
        return desiredJSON.isPresent();
    }

    @Override
    public void sendRequest() throws HTTPException, IOException, JSONIsNullException {
        if (desiredJSON.isPresent()){
            sendJSONToURL(desiredJSON.get());
        } else {
            throw new JSONIsNullException();
        }
    }

    @Override
    public void run(){
        if (!canRun()){
            log.warn("Attempted to set JSON that cannot be set");
        } else {
            sendRequestAndHandleAllErrors();
        }
    }

    /**
     *
     * @param connection The connection to configure
     * @throws IOException If the connection cannot be configured properly
     */
    private void configureConnection(URLConnection connection) throws IOException {
        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(Boolean.TRUE);
    }

    private void sendJSONToURL(T desiredJSON) throws HTTPException, IOException {
        URLConnection connection = openConnection(this.getURL());
        configureConnection(connection);

        try {
            connection.connect();
            mapper.writeValue(connection.getOutputStream(), desiredJSON);
        } finally {
            connection.disconnect();
        }
    }

    private void sendRequestAndHandleAllErrors(){
        try {
            sendRequest();
        } catch (RuntimeException error){
            log.error("Attempting to set JSON asynchronously threw runtime error", error);
            throw error;
        } catch (Exception error){
            log.error("Attempting to send request asynchronously threw error", error);
        }
    }

    private static URLConnection openConnection(URL url) throws IOException {
        try {
            return url.openConnection();
        } catch (HTTPConnectionCastException error){
            log.error("Attempting to cast threw error", error);
            throw new IOException(error);
        }
    }
}
