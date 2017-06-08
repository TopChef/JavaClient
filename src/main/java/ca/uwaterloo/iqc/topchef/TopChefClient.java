package ca.uwaterloo.iqc.topchef;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoints.*;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import ca.uwaterloo.iqc.topchef.exceptions.ServiceNotFoundException;
import ca.uwaterloo.iqc.topchef.url_resolver.Resolver;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

/**
 * Base class for interacting with the TopChef API
 */
public class TopChefClient implements Client {
    /**
     * The URL of the API
     */
    private final URL url;

    /**
     *
     * @param url The URL to use for talking to the API
     */
    public TopChefClient(URL url){
        this.url = url;
    }

    /**
     *
     * @param url The URL, formatted as a {@link java.net.URL}
     */
    public TopChefClient(java.net.URL url){
        this.url = new ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL(url);
    }

    /**
     *
     * @param url The URL, formatted as a string
     * @throws MalformedURLException If the string is not a URL
     */
    public TopChefClient(String url) throws MalformedURLException {
        this.url = new ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL(url);
    }

    /**
     *
     * @return The URL
     */
    @Override
    public URL getURL(){
        return this.url;
    }

    /**
     *
     * @return The resolver for finding endpoints for the API
     */
    @Override
    public URLResolver getURLResolver(){
        return new Resolver(this.url);
    }

    /**
     *
     * @return A validator for comparing JSON objects against JSON Schema
     */
    @Override
    public Validator getJSONSchemaValidator(){
        return new JSONSchemaValidator(this);
    }

    @Override
    public List<Service> getServices() throws HTTPException, IOException {
        Services serviceEndpoint = new ServicesEndpoint(this);
        return serviceEndpoint.getServices();
    }

    @Override
    public Service getService(UUID id) throws IOException, ServiceNotFoundException {
        Services serviceEndpoint = new ServicesEndpoint(this);
        return serviceEndpoint.getServiceByUUID(id);
    }

    @Override
    public Service getService(String id) throws IOException, ServiceNotFoundException, IllegalArgumentException {
        Services endpoint = new ServicesEndpoint(this);
        return endpoint.getServiceByUUID(id);
    }
}
