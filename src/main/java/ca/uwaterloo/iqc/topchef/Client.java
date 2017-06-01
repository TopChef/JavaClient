package ca.uwaterloo.iqc.topchef;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoint_models.Validator;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;

/**
 * Root interface for the client. Provides an entry point to the client
 */
public interface Client {
    /**
     *
     * @return The resolver to use for the client
     */
    URLResolver getURLResolver();

    /**
     *
     * @return The base URL where the API is located
     */
    URL getURL();

    /**
     *
     * @return The endpoint used to validate JSON against a JSON schema
     */
    Validator getJSONSchemaValidator();
}
