package ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper.url_connection;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL;
import ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper.AbstractWrapperTestCase;
import org.junit.Before;

/**
 * Base class for integration tests of {@link ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URLConnection}
 */
public abstract class AbstractURLConnectionTestCase extends AbstractWrapperTestCase {
    /**
     * The good connection. In order for this test to pass, it is assumed that this connection is possible.
     */
    protected URLConnection connection;

    /**
     * Open the connection prior to testing business logic in the
     * {@link ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URLConnection}
     *
     * @throws RuntimeException If the connection cannot be opened for whatever reason
     */
    @Before
    public void setConnection() throws RuntimeException {
        try {
            URL url = new URL(goodURLString);
            connection = url.openConnection();
        } catch (Exception error) {
            throw new RuntimeException(
                    String.format("Unable to create connection to URL %s", goodURLString)
            );
        }
    }
}
