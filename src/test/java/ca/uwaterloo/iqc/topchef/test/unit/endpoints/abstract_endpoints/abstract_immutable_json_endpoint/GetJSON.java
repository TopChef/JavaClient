package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_immutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.ImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jetbrains.annotations.Contract;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

/**
 * Contains unit tests for {@link AbstractImmutableJSONEndpoint#getJSON()}
 */
@RunWith(JUnitQuickcheck.class)
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

    /**
     * Test that the code reports exceptions resulting from an unexpected HTTP response code
     * @param code The randomly-generated HTTP code to test on
     * @throws Exception if the underlying test throws an exception
     */
    @Property
    public void unexpectedErrorCode(
            @From(HTTPResponseCodeGenerator.class) HTTPResponseCode code
    ) throws Exception {
        Assume.assumeTrue(isErrorCode(code));
        Mockery context = new Mockery();
        URL mockUrl = context.mock(URL.class);
        URLConnection mockConnection = context.mock(URLConnection.class);

        context.checking(new ExpectationsForBadHTTPCode(code, mockUrl, mockConnection));
        expectedException.expect(HTTPException.class);

        AbstractImmutableJSONEndpoint endpoint = new ConcreteImmutableJSONEndpoint(mockUrl);
        endpoint.getJSON();

        context.assertIsSatisfied();
    }

    /**
     *
     * @param code The HTTP code to check
     * @return True if the code is an HTTP error code, otherwise False
     */
    @Contract(pure = true)
    private boolean isErrorCode(HTTPResponseCode code){
        boolean isErrorCode = true;

        switch (code) {
            case OK:
                isErrorCode = false;
                break;
            case ACCEPTED:
                isErrorCode = false;
                break;
            case CREATED:
                isErrorCode = false;
                break;
            case NO_CONTENT:
                isErrorCode = false;
                break;
        }

        return isErrorCode;
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
            oneOf(mockConnection).close();

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

    /**
     * The expected behaviour for the stubs when an error code is recieved from GetJSON
     */
    private final class ExpectationsForBadHTTPCode extends Expectations {

        /**
         * The error code that the mock connection should return
         */
        private final HTTPResponseCode codeToReturn;

        /**
         * The URL that will return the error code
         */
        private final URL mockURL;

        /**
         * The connection to the erroneous URL
         */
        private final URLConnection mockConnection;

        /**
         *
         * @param code The error code to return
         * @param mockURL The mock representation of the URL
         * @param mockConnection The mock representation of the connection to the mock URL
         * @throws Exception If the underlying method throws an exception for whatever reason
         */
        public ExpectationsForBadHTTPCode(
                HTTPResponseCode code,
                URL mockURL, URLConnection mockConnection
        ) throws Exception {
            this.codeToReturn = code;
            this.mockURL = mockURL;
            this.mockConnection = mockConnection;

            oneOf(this.mockURL).openConnection();
            will(returnValue(this.mockConnection));

            setExpectationsForConnection();
        }

        /**
         *
         * Set the connection up for returning the bad server response
         *
         * @throws Exception If the underlying methods throw an exception
         */
        private void setExpectationsForConnection() throws Exception {
            oneOf(this.mockConnection).connect();
            oneOf(this.mockConnection).setRequestProperty("Content-Type", "application/json");
            oneOf(this.mockConnection).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(this.mockConnection).setDoOutput(Boolean.FALSE);

            oneOf(this.mockConnection).getResponseCode();
            will(returnValue(codeToReturn));

            oneOf(this.mockConnection).close();
        }
    }
}
