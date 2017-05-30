package ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

/**
 * Wraps the URL implementation in {@link java.net.URL}
 */
public final class URL implements ca.uwaterloo.iqc.topchef.adapters.java.net.URL {
    /**
     * The application log.
     */
    private static final Logger log = LoggerFactory.getLogger(URL.class);

    /**
     * The instance of {@link java.net.URL} that is being managed by this wrapper. Connections are handled by this
     * instance.
     */
    private final java.net.URL wrappedURL;

    /**
     *
     * @param url The URL to which a connection is to be made
     * @throws MalformedURLException if the input parameter is not a valid URL
     */
    public URL(String url) throws MalformedURLException {
        this.wrappedURL = new java.net.URL(url);
    }

    /**
     * Bypass the Malformed URL check.
     *
     * @param url The instance of {@link java.net.URL} that this manager will wrap.
     */
    public URL(java.net.URL url){
        this.wrappedURL = url;
    }

    /**
     *
     * @return An open {@link URLConnection}
     * @throws IOException if the underlying connection could not be opened
     * @throws HTTPConnectionCastException if the connection cannot be cast to an {@link HttpURLConnection}
     */
    @Override
    public URLConnection openConnection() throws IOException, HTTPConnectionCastException {
        java.net.URLConnection connection = this.wrappedURL.openConnection();

        HttpURLConnection http_connection = castToHTTPConnection(connection);

        return new ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URLConnection(http_connection);
    }

    /**
     *
     * @param connection The connection to cast
     * @return The casted connection instance
     * @throws HTTPConnectionCastException If the connection could not be cast
     */
    private static HttpURLConnection castToHTTPConnection(java.net.URLConnection connection)
            throws HTTPConnectionCastException {
        try {
            return (HttpURLConnection) connection;
        } catch (ClassCastException error){
            log.error("Unable to cast HTTP Connection", error);
            throw new HTTPConnectionCastException(error);
        }
    }
}
