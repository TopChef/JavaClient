package ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper.url_connection;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URLConnection;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ProtocolException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Contains unit tests for {@link URLConnection#getResponseCode()}
 */
public final class GetResponseCode extends AbstractURLConnectionTestCase {
    /**
     * Set the method to a GET request. If it doesn't work, then fail the test.
     */
    @Before
    public void setUp(){
        try {
            connection.setRequestMethod(HTTPRequestMethod.GET);
        } catch (ProtocolException error){
            throw new RuntimeException("Unable to set method to GET. Test stopped", error);
        }
    }

    /**
     * Tests that running a good connection returns the proper status code.
     */
    @Test
    public void testGoodStatusCode(){
        try {
            assertEquals(HTTPResponseCode.OK, connection.getResponseCode());
        } catch (IOException error){
            fail("Unable to get response. IOException was thrown.");
        }
    }
}
