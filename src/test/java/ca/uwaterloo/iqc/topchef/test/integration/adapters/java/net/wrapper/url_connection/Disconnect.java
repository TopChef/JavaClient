package ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper.url_connection;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URLConnection;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains unit tests for {@link URLConnection#disconnect()}
 */
public final class Disconnect extends AbstractURLConnectionTestCase {
    private static final Logger log = LoggerFactory.getLogger(Disconnect.class);

    @Before
    public void setUpConnection(){
        try {
            connection.setRequestMethod(HTTPRequestMethod.GET);
            connection.connect();
        } catch (Exception error){
            log.error("Error thrown", error);
        }
    }

    @Test
    public void disconnect(){
        connection.disconnect();
    }
}
