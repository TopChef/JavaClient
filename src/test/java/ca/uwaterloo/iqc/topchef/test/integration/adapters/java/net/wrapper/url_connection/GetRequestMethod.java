package ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper.url_connection;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URLConnection#getRequestMethod()}
 */
public final class GetRequestMethod extends AbstractURLConnectionTestCase {
    @Test
    public void getDefaultRequestMethod(){
        assertEquals(HTTPRequestMethod.GET, connection.getRequestMethod());
    }
}
