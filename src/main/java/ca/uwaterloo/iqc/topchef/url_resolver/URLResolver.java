package ca.uwaterloo.iqc.topchef.url_resolver;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;

import java.util.UUID;

/**
 * Describes the contract for resolving URLs in the TopChef API.
 */
public interface URLResolver {

    /**
     *
     * @return The base URL for this resolver
     */
    URL getBaseURL();

    /**
     *
     * @return a URL to the JSON Schema Validator
     */
    URL getValidatorEndpoint();

    /**
     *
     * @return a URL to the /services endpoint
     */
    URL getServicesEndpoint();

    URL getServiceEndpoint(UUID serviceID);

    URL getServiceEndpoint(String serviceID);

    URL getQueueEndpointForService(UUID serviceID);

    URL getJobsEndpointForService(UUID serviceID);

    URL getJobEndpoint(UUID jobID);
}
