package ca.uwaterloo.iqc.topchef.test.unit.endpoint_models.abstract_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoint_models.AbstractEndpoint;
import ca.uwaterloo.iqc.topchef.endpoint_models.Endpoint;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Contains unit tests for {@link AbstractEndpoint#isEndpointUp()}
 */
public final class IsEndpointUp extends AbstractAbstractEndpointTestCase {
    /**
     * The mocking context to use in this test
     */
    private static final Mockery context = new Mockery();

    /**
     * A mock URL
     */
    private static final URL mockURL = context.mock(URL.class);

    /**
     * The mock connection to the mock URL.
     */
    private static final URLConnection mockConnection = context.mock(URLConnection.class);

    /**
     *
     * @throws Exception Shouldn't really. It's for creating the test expectations
     */
    @Test
    public void isEndpointUp() throws Exception {
        context.checking(new ExpectationsForTest());

        Endpoint endpoint = new ConcreteEndpoint(mockURL);
        assertTrue(endpoint.isEndpointUp());

        context.assertIsSatisfied();
    }

    /**
     * The expectations out of the mock objects in the test
     */
    private static final class ExpectationsForTest extends Expectations{
        /**
         *
         * Declare the expectations for this test
         *
         * @throws Exception for tyoe checking reasons
         */
        public ExpectationsForTest() throws Exception {
            super();
            oneOf(mockURL).openConnection();
            will(returnValue(mockConnection));

            oneOf(mockConnection).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(mockConnection).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));
        }
    }
}
