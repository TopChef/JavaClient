package ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper.url_connection;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import org.junit.Test;

import java.net.ProtocolException;

import static org.junit.Assert.fail;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URLConnection#setRequestMethod(HTTPRequestMethod)}
 */
public final class SetRequestMethod extends AbstractURLConnectionTestCase {

    /**
     * Run the test for a GET request
     */
    @Test
    public void setMethodToGet(){
        testSetMethod(HTTPRequestMethod.GET);
    }

    /**
     * Run the test for a POST request
     */
    @Test
    public void setMethodPost(){
        testSetMethod(HTTPRequestMethod.POST);
    }

    /**
     * Run the setter for a PATCH request
     */
    @Test
    public void setMethodPatch(){
        testSetMethod(HTTPRequestMethod.PATCH);
    }

    /**
     * Run the test for a PUT request
     */
    @Test
    public void setMethodPut(){
        testSetMethod(HTTPRequestMethod.PUT);
    }

    /**
     * If setting the method fails, fail the test
     *
     * @param method The method to set
     */
    private void testSetMethod(HTTPRequestMethod method){
        try {
            connection.setRequestMethod(method);
        } catch (ProtocolException error){
            fail(
                    String.format("Unable to set method to %s", method)
            );
        }
    }
}
