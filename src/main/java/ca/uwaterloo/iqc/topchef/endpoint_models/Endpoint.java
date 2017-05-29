package ca.uwaterloo.iqc.topchef.endpoint_models;

import java.net.ProtocolException;
import java.net.URL;

/**
 * Describes the contract for an HTTP endpoint
 */
public interface Endpoint {

    /**
     *
     * @return The URL of this endpoint
     */
    URL getURL();

    /**
     *
     * @return True if the endpoint is up, and False if not
     */
    Boolean isEndpointUp() throws ProtocolException;
}
