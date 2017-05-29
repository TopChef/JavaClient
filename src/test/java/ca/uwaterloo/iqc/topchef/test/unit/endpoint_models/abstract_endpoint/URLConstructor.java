package ca.uwaterloo.iqc.topchef.unit.endpoint_models.abstract_endpoint;

import ca.uwaterloo.iqc.topchef.endpoint_models.Endpoint;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoint_models.AbstractEndpoint#AbstractEndpoint(URL)}
 */
public final class URLConstructor extends AbstractAbstractEndpointTestCase {
    private static final Logger log = LoggerFactory.getLogger(URLConstructor.class);

    private URL url;

    @Before
    public void createValidURL(){
        try {
            this.url = new URL("http://localhost:5000");
        } catch (MalformedURLException error){
            log.error("Bad URL for testing", error);
        }
    }

    @Test
    public void createObject(){
        Endpoint endpoint = new ConcreteEndpoint(this.url);
        assertEquals(this.url, endpoint.getURL());
    }
}
