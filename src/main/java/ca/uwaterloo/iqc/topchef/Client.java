package ca.uwaterloo.iqc.topchef;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoints.Service;
import ca.uwaterloo.iqc.topchef.endpoints.Validator;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import ca.uwaterloo.iqc.topchef.exceptions.ServiceNotFoundException;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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


    List<Service> getServices() throws HTTPException, IOException;

    Service getService(UUID serviceID) throws IOException, ServiceNotFoundException;

    Service getService(String serviceID) throws IOException, ServiceNotFoundException, IllegalArgumentException;
}
