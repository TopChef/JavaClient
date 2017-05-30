package ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper.url;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests that a connection successfully opens to a working URL
 */
public final class OpenConnection extends AbstractURLTestCase {
    private URL goodURL;

    @Before
    public void setGoodURL() throws RuntimeException {
        try {
            goodURL = new ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL(goodURLString);
        } catch (MalformedURLException error) {
            throw new RuntimeException(error);
        }
    }

    @Test
    public void openConnectionGoodURL(){
        try {
            URLConnection connection = goodURL.openConnection();
            assertEquals(HTTPResponseCode.OK, connection.getResponseCode());
        } catch (Exception error){
            fail("Unable to open connection to a working URL");
        }
    }
}
