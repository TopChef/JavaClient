package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_immutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.ImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

/**
 * Contains unit tests for {@link AbstractImmutableJSONEndpoint#getJSON()}
 */
public final class GetJSON extends AbstractImmutableJSONEndpointTestCase {
    private final Mockery context = new Mockery();

    private final URL mockURL = context.mock(URL.class);

    private final URLConnection mockConnection = context.mock(URLConnection.class);

    private ImmutableJSONEndpoint endpoint;

    private InputStream mockInputStream;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setEndpoint(){
        endpoint = new ConcreteImmutableJSONEndpoint(mockURL);
    }

    @Before
    public void setMockInputStream(){
        mockInputStream = new ByteArrayInputStream("".getBytes());
    }

    @Test
    public void getJSONHappyPath() throws Exception {
        context.checking(new ExpectationsForGetJSONHappyPath());

        assertNotNull(endpoint.getJSON());

        context.assertIsSatisfied();
    }

    @Test
    public void badConnectionAssert() throws Exception {
        context.checking(new ExpectationsForBadConnection());
        expectedException.expect(HTTPException.class);

        endpoint.getJSON();

        context.assertIsSatisfied();
    }

    private abstract class ExpectationsForTest extends Expectations {
        public ExpectationsForTest() throws Exception {
            oneOf(mockURL).openConnection();
            will(returnValue(mockConnection));

            setExpectationsForConnection();
        }

        protected abstract void setExpectationsForConnection() throws Exception;
    }

    private final class ExpectationsForGetJSONHappyPath extends ExpectationsForTest {
        public ExpectationsForGetJSONHappyPath() throws Exception {
            super();
        }

        @Override
        protected void setExpectationsForConnection() throws Exception {
            oneOf(mockConnection).connect();
            oneOf(mockConnection).setRequestProperty("Content-Type", "application/json");
            oneOf(mockConnection).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(mockConnection).setDoOutput(Boolean.FALSE);
            oneOf(mockConnection).disconnect();

            setExpectationsForResponseCode();
            setExpectationsForInputStream();
        }

        private void setExpectationsForResponseCode() throws Exception {
            oneOf(mockConnection).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));
        }

        private void setExpectationsForInputStream() throws Exception {
            mockInputStream = new ByteArrayInputStream("{\"data\" : \"json\"}".getBytes());
            oneOf(mockConnection).getInputStream();
            will(returnValue(mockInputStream));
        }
    }

    private final class ExpectationsForBadConnection extends ExpectationsForTest {
        public ExpectationsForBadConnection() throws Exception {
            super();
        }

        @Override
        protected void setExpectationsForConnection() throws Exception {
            oneOf(mockConnection).connect();
            oneOf(mockConnection).setRequestProperty("Content-Type", "application/json");
            oneOf(mockConnection).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(mockConnection).setDoOutput(Boolean.FALSE);

            oneOf(mockConnection).getResponseCode();
            will(returnValue(HTTPResponseCode.NOT_FOUND));

            oneOf(mockConnection).disconnect();
        }
    }
}
