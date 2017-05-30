package ca.uwaterloo.iqc.topchef.adapters.java.net;

import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;

import java.io.IOException;

/**
 * Describes a contract for a Java URL
 */
public interface URL {
    /**
     * Open a connection
     */
    URLConnection openConnection() throws IOException, HTTPConnectionCastException;
}
