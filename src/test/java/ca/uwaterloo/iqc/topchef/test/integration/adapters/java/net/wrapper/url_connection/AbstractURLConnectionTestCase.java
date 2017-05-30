package ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper.url_connection;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL;
import ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper.AbstractWrapperTestCase;
import org.junit.Before;

/**
 * Base class for unit tests of {@link ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URLConnection}
 */
public abstract class AbstractURLConnectionTestCase extends AbstractWrapperTestCase {
    protected URLConnection connection;

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
