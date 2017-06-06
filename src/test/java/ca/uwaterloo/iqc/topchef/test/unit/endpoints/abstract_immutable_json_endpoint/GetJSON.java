package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_immutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.AbstractImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.endpoints.ImmutableJSONEndpoint;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static junit.framework.Assert.assertTrue;

/**
 * Contains unit tests for {@link AbstractImmutableJSONEndpoint#getJSON()}
 */
public final class GetJSON extends AbstractImmutableJSONEndpointTestCase {
    private final Mockery context = new Mockery();

    private final URL mockURL = context.mock(URL.class);

    private final URLConnection mockConnection = context.mock(URLConnection.class);

    private ImmutableJSONEndpoint endpoint;

    private InputStream mockInputStream;

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

        assertTrue(endpoint.getJSON() != null);

        context.assertIsSatisfied();
    }

    private abstract class ExpectationsForTest extends Expectations {
        public ExpectationsForTest() throws Exception {
            oneOf(mockURL).openConnection();
            will(returnValue(mockConnection));

            setExpectationsForConnection();
        }

        private void setExpectationsForConnection() throws Exception {
            oneOf(mockConnection).connect();
            oneOf(mockConnection).setRequestProperty("Content-Type", "application/json");
            oneOf(mockConnection).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(mockConnection).setDoOutput(Boolean.FALSE);
            oneOf(mockConnection).disconnect();

            setExpectationsForResponseCode();
            setExpectationsForInputStream();
        }

        protected abstract void setExpectationsForResponseCode() throws Exception;

        protected abstract void setExpectationsForInputStream() throws Exception;
    }

    private final class ExpectationsForGetJSONHappyPath extends ExpectationsForTest {
        public ExpectationsForGetJSONHappyPath() throws Exception {
            super();
        }

        @Override
        protected void setExpectationsForResponseCode() throws Exception {
            oneOf(mockConnection).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));
        }

        @Override
        protected void setExpectationsForInputStream() throws Exception {
            mockInputStream = new ByteArrayInputStream("{\"data\" : \"json\"}".getBytes());
            oneOf(mockConnection).getInputStream();
            will(returnValue(mockInputStream));
        }
    }
}
