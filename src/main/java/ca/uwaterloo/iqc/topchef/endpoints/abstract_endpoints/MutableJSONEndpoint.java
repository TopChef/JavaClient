package ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.RequiresJSONMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import com.github.dmstocking.optional.java.util.Optional;

import java.io.IOException;

/**
 * Describes an endpoint to which JSON can be sent
 *
 * @param <T> The class used to interact with the JSON
 */
public interface MutableJSONEndpoint<T> extends Endpoint, Runnable, RequiresJSONMapper {
    /**
     *
     * @return The HTTP method used to set the JSON
     */
    HTTPRequestMethod getRequestMethod();

    /**
     *
     * @param desiredMethod The method to use when making the HTTP request
     *                      to set the JSON
     */
    void setRequestMethod(HTTPRequestMethod desiredMethod);

    /**
     *
     * @return The instance that will be used to set JSON, if it exists
     */
    Optional<T> getDesiredJSON();

    /**
     *
     * @param desiredJSON The JSON object that will be written to this request
     */
    void setDesiredJSON(T desiredJSON);

    /**
     *
     * @return {@link Boolean#TRUE} if the request can successfully be sent
     */
    Boolean canRun();

    /**
     * Run the request, throwing any checked exceptions if necessary
     *
     * @throws HTTPException If HTTP goes wrong
     * @throws IOException If some I/O breaks somewhere
     */
    void sendRequest() throws HTTPException, IOException;

    /**
     * Run the request with generic handlers for any exceptions to be thrown.
     * This method is allowed to eat exceptions, so implementations should provide some
     * way to let the user know that things broke.
     */
    void run();
}
