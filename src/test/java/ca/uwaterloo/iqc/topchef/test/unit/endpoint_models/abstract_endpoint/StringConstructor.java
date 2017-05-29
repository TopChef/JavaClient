package ca.uwaterloo.iqc.topchef.unit.endpoint_models.abstract_endpoint;

import ca.uwaterloo.iqc.topchef.endpoint_models.Endpoint;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Contains tests for {@link ca.uwaterloo.iqc.topchef.endpoint_models.AbstractEndpoint#AbstractEndpoint(String)}
 */
public final class StringConstructor extends AbstractAbstractEndpointTestCase {
    private static final String goodEndpoint = "http://www.google.com";
    private static final String badEndpoint = "Not a valid URL";

    @Test
    public void goodEndpointTest(){
        Endpoint endpoint;
        URL endpointURL;

        try {
            endpoint = new ConcreteEndpoint(goodEndpoint);
            endpointURL = endpoint.getURL();

        } catch (MalformedURLException error) {
            throw new AssertionError(
                String.format(
                        "The test for the good endpoint threw an error %s",
                        error
                )
            );
        }

        assertEquals(goodEndpoint, endpointURL.toString());
    }

    @Test
    public void badEndpointTest() throws Exception {
        exception.expect(MalformedURLException.class);
        new ConcreteEndpoint(badEndpoint);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();
}
