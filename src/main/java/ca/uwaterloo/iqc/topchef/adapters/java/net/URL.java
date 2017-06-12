package ca.uwaterloo.iqc.topchef.adapters.java.net;

import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Describes a contract for a Java URL, and provides a means to build relative URLs out of a base URL.
 */
public interface URL extends Comparable<URL> {
    /**
     * Open a connection
     */
    URLConnection openConnection() throws IOException, HTTPConnectionCastException;

    /**
     *
     * @param path The text to append to the URL
     * @return A URL with the path appended to ut
     * @throws MalformedURLException If the appended string will not form a URL
     */
    URL getRelativeURL(String path) throws MalformedURLException;

    /**
     *
     * @return A string representation of this URL
     */
    @Contract(pure = true)
    @NotNull
    String toString();
}
