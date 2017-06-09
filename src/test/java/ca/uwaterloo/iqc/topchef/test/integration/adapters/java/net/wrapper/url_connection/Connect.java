package ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper.url_connection;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URLConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ProtocolException;

import static org.junit.Assert.fail;

/**
 * Contains integration tests for {@link URLConnection#connect()}
 */
public final class Connect extends AbstractURLConnectionTestCase {
    /**
     * The log to which error reports for the test will be written.
     */
    private static final Logger log = LoggerFactory.getLogger(Connect.class);

    /**
     * Ensure that the request to be tested is a GET request. GET requests to the target
     * are idempotent, and so are the most guaranteed to work.
     *
     * @throws RuntimeException If the setup fails
     */
    @Before
    public void prepareConnection() throws RuntimeException {
        try {
            connection.setRequestMethod(HTTPRequestMethod.GET);
        } catch (ProtocolException error){
            throw new RuntimeException(error);
        }
    }

    /**
     * Clean up the connection after the test
     */
    @After
    public void disconnect(){
        connection.close();
    }

    /**
     * Check that the connection works as intended.
     */
    @Test
    public void connect(){
        try {
            connection.connect();
        } catch (IOException error){
            log.error("IOException thrown", error);
            fail("IOException thrown on allgedly good URL. Test failed");
        } catch (IllegalStateException error){
            log.error("IllegalStateException thrown", error);
            fail("IllegalStateException thrown in supposedly legal state. Test failed");
        }
    }
}
