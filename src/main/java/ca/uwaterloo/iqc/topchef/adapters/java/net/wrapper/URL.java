package ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

/**
 * Wraps the URL implementation in {@link java.net.URL}
 */
public final class URL implements ca.uwaterloo.iqc.topchef.adapters.java.net.URL {
    private final java.net.URL wrappedURL;

    public URL(String url) throws MalformedURLException {
        this.wrappedURL = new java.net.URL(url);
    }

    public URL(java.net.URL url){
        this.wrappedURL = url;
    }

    @Override
    public URLConnection openConnection() throws IOException, HTTPConnectionCastException {
        java.net.URLConnection connection = this.wrappedURL.openConnection();

        HttpURLConnection http_connection = castToHTTPConnection(connection);

        return new ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URLConnection(http_connection);
    }

    private static HttpURLConnection castToHTTPConnection(java.net.URLConnection connection)
            throws HTTPConnectionCastException {
        try {
            return (HttpURLConnection) connection;
        } catch (ClassCastException error){
            throw new HTTPConnectionCastException(error);
        }
    }
}
