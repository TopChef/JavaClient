package ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;

import java.io.IOException;

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
    Boolean isEndpointUp() throws IOException, HTTPConnectionCastException;
}
