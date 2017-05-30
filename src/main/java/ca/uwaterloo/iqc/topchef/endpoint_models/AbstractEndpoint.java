package ca.uwaterloo.iqc.topchef.endpoint_models;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;

import java.io.IOException;

/**
 * Capable of querying for an endpoint to determine if it is alive or not
 */
public abstract class AbstractEndpoint implements Endpoint {

    /**
     * Stores the URL at which this endpoint is located
     */
    private final URL url;

    /**
     * @param url The URL of this endpoint
     */
    public AbstractEndpoint(URL url){
        this.url = url;
    }

    /**
     *
     * @return The URL of this API endpoint
     */
    @Override
    public URL getURL() {
        return this.url;
    }

    /**
     *
     * @return {@link Boolean#TRUE} if the connection can be made and {@link Boolean#FALSE} if not
     * @throws IOException If the connection could not be opened
     * @throws HTTPConnectionCastException If the connection managed by this URL is not an HTTP connection
     */
    @Override
    public Boolean isEndpointUp() throws IOException, HTTPConnectionCastException {
        URLConnection connection = this.url.openConnection();
        connection.setRequestMethod(HTTPRequestMethod.GET);
        return HTTPResponseCode.OK.equals(connection.getResponseCode());
    }
}
