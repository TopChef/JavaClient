package ca.uwaterloo.iqc.topchef.endpoint_models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Capable of querying for an endpoint to determine if it is alive or not
 */
public abstract class AbstractEndpoint implements Endpoint {

    private static final Logger log = LoggerFactory.getLogger(AbstractEndpoint.class);

    private static final String HTTP_GET = "GET";

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
     * @param url The URL of this endpoint
     *
     * @throws MalformedURLException If the URL is not a URL
     */
    public AbstractEndpoint(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    @Override
    public URL getURL() {
        return this.url;
    }

    @Override
    public Boolean isEndpointUp() throws ProtocolException {

        URLConnection connection;
        HttpURLConnection http_connection;

        try {
            connection = this.url.openConnection();
        } catch (IOException error) {
            this.handleBadConnectionMessage(error);
            return Boolean.FALSE;
        }

        try {
            http_connection = (HttpURLConnection) connection;
        } catch (ClassCastException error) {
            log.error(this.makeUnableToCastToHTTPMessage(error));
            throw error;
        }

        http_connection.setRequestMethod(HTTP_GET);

        Integer status_code;

        try {
            http_connection.connect();
            status_code = http_connection.getResponseCode();
        } catch (IOException error) {
            this.handleBadConnectionMessage(error);
            return Boolean.FALSE;
        }

        if (status_code == HTTP_OK){
            return Boolean.TRUE;
        } else {
            this.handleResponseNot200(status_code);
            return Boolean.FALSE;
        }
    }

    private String makeUnableToCastToHTTPMessage(ClassCastException error){
        return String.format(
                "Connection %s could not be cast to an HTTP connection. Error: %s",
                this, error
        );
    }

    private void handleBadConnectionMessage(IOException error){
        log.warn(
                String.format(
                    "Attempting to connect to endpoint %s returned error %s",
                    this.url, error
                )
        );
    }

    private void handleResponseNot200(Integer status_code){
        log.warn(
                String.format(
                        "Attempting to connect to %s returned status code %s",
                        this.url, status_code
                )
        );
    }
}
