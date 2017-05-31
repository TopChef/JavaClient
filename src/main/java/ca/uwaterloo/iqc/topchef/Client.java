package ca.uwaterloo.iqc.topchef;

import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;

/**
 * Root interface for the client. Provides an entry point to the client
 */
public interface Client {
    URLResolver getURLResolver();
}
