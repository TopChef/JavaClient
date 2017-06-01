package ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * The regex for a URL starting with a '/'. This is used to strip the forward slash from the URL.
     */
    private static final Pattern forwardSlashRegex = Pattern.compile("(?<=\\/).*$");

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
     * @param path The text to append to the URL
     * @return The appended URL
     * @throws MalformedURLException if the appended URL is not a URL
     */
    @Contract("_ -> !null")
    @Override
    public ca.uwaterloo.iqc.topchef.adapters.java.net.URL getRelativeURL(String path) throws MalformedURLException {
        return new URL(String.format(
                "%s://%s:%s/%s",
                this.wrappedURL.getProtocol(), this.wrappedURL.getHost(),
                this.wrappedURL.getPort(), stripForwardSlash(path)
                ));
    }

    /**
     * Check for equality between two URLs. Two URLs are equal if they point to the same resource
     * @param otherURL The URL to compare to
     * @return the result of the comparison function of the URLs as a string
     */
    @Override
    @Contract(pure = true)
    public int compareTo(@NotNull ca.uwaterloo.iqc.topchef.adapters.java.net.URL otherURL){
        return this.toString().compareTo(otherURL.toString());
    }

    /**
     *
     * @return The string representation of this URL
     */
    @NotNull
    @Override
    @Contract(pure = true)
    public String toString(){
        return this.wrappedURL.toString();
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

    /**
     * If the appended text started with a forward slash, remove it
     * @param path The text for which the slash is removed
     * @return The modified string
     */
    private static String stripForwardSlash(String path){
        Matcher match = forwardSlashRegex.matcher(path);
        if (match.find()) {
            return match.group(0);
        } else {
            return path;
        }
    }
}
