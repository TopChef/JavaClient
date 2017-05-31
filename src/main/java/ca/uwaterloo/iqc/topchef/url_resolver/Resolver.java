package ca.uwaterloo.iqc.topchef.url_resolver;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * An implementation of the {@link URLResolver}, used to match URLs in the API.
 */
public class Resolver implements URLResolver {
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
     * @return The URL for the API's JSON schema validator relative to the base URL
     * @throws RuntimeException If the URL cannot be formed
     */
    @Override
    public URL getValidatorEndpoint() throws RuntimeException {
        try {
            return this.baseURL.getRelativeURL("/validator");
        } catch (MalformedURLException error) {
            log.error("Joining URLs threw unexpected exception", error);
            throw new RuntimeException(error);
        }
    }
}