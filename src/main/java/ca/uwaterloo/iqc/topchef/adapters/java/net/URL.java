package ca.uwaterloo.iqc.topchef.adapters.java.net;

import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Describes a contract for a Java URL
 */
public interface URL extends Comparable<URL> {
    /**
     * Open a connection
     */
    URLConnection openConnection() throws IOException, HTTPConnectionCastException;

    URL getRelativeURL(String path) throws MalformedURLException;

    @Contract(pure = true)
    @NotNull
    String toString();
}
