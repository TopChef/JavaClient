package ca.uwaterloo.iqc.topchef.url_resolver;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.UUID;

/**
 * An implementation of the {@link URLResolver}, used to match URLs in the API.
 */
public class Resolver implements URLResolver {

    /**
     * The log to which
     */
    private static final Logger log = LoggerFactory.getLogger(Resolver.class);

    private final URL baseURL;

    /**
     * @param baseURL The top-level endpoint for the API
     */
    public Resolver(URL baseURL) {
        this.baseURL = baseURL;
    }

    /**
     *
     * @return The base URL of the resolver
     */
    @Override
    public URL getBaseURL(){
        return baseURL;
    }

    /**
     *
     * @return The URL for the API's JSON schema validator relative to the base URL
     * @throws IllegalStateException If the URL cannot be formed
     */
    @Override
    public URL getValidatorEndpoint() throws IllegalStateException {
        try {
            return this.baseURL.getRelativeURL("/validator");
        } catch (MalformedURLException error) {
            log.error("Joining URLs threw unexpected exception", error);
            throw new IllegalStateException(error);
        }
    }

    /**
     *
     * @return A URL for the endpoint where a list of services can be retrieved
     * @throws IllegalStateException If the resulting endpoint cannot be formed, due to
     *  a malformed URL. Since the incoming URL is valid, and the resulting URL should
     *  be valid, this is a serious state violation, and worthy of an {@link IllegalStateException}
     */
    @Override
    public URL getServicesEndpoint() throws IllegalStateException {
        try {
            return this.baseURL.getRelativeURL("/services");
        } catch (MalformedURLException error) {
            log.error("Joining URLs threw unexpected exception", error);
            throw new IllegalStateException(error);
        }
    }

    /**
     *
     * @param service The ID of the service for which the endpoint is to be retrieved
     * @return A URL to the API endpoint for a particular service
     * @throws IllegalStateException If the resulting URL is bad
     */
    @Override
    public URL getServiceEndpoint(UUID service) throws IllegalStateException {
        try {
            return this.baseURL.getRelativeURL(String.format("/services/%s", service.toString()));
        } catch (MalformedURLException error) {
            log.error("Joining URLs threw unexpected exception", error);
            throw new IllegalStateException(error);
        }
    }

    /**
     *
     * @param service The ID of the service for which the URL is to be retrieved
     * @return A URL to the endpoint for a particular service
     * @throws IllegalStateException If the resulting URL is malformed
     * @throws IllegalArgumentException If the incoming string cannot be made into a UUID.
     */
    @Override
    public URL getServiceEndpoint(String service) throws IllegalStateException, IllegalArgumentException {
        return this.getServiceEndpoint(UUID.fromString(service));
    }
}
