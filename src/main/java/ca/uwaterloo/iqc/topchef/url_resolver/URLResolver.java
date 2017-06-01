package ca.uwaterloo.iqc.topchef.url_resolver;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;

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
}
