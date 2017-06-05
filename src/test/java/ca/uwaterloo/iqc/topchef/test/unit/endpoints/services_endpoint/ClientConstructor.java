package ca.uwaterloo.iqc.topchef.test.unit.endpoints.services_endpoint;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoints.Endpoint;
import ca.uwaterloo.iqc.topchef.endpoints.ServicesEndpoint;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoints.ServicesEndpoint#ServicesEndpoint(Client)}
 */
public final class ClientConstructor extends AbstractServicesEndpointTestCase {
    private static final Mockery context = new Mockery();

    private static final Client mockClient = context.mock(Client.class);

    private static final URLResolver mockResolver = context.mock(URLResolver.class);

    private static final URL mockServicesEndpoint = context.mock(URL.class);

    @Test
    public void clientConstructor(){
        context.checking(new ExpectationsForTest());

        Endpoint endpoint = new ServicesEndpoint(mockClient);
        assertEquals(mockServicesEndpoint, endpoint.getURL());

        context.assertIsSatisfied();
    }

    private final class ExpectationsForTest extends Expectations {
        public ExpectationsForTest(){
            oneOf(mockClient).getURLResolver();
            will(returnValue(mockResolver));

            oneOf(mockResolver).getServicesEndpoint();
            will(returnValue(mockServicesEndpoint));
        }
    }
}
