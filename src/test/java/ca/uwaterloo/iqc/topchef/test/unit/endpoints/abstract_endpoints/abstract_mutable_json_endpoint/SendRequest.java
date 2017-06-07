package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_mutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractMutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.MutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.JSONIsNullException;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Contains unit tests for
 * {@link AbstractMutableJSONEndpoint#sendRequest()}
 */
@RunWith(JUnitQuickcheck.class)
public final class SendRequest extends AbstractAbstractMutableJSONEndpointTestCase {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void jsonIsNullTest() throws Exception {
        Mockery context = new Mockery();
        URL mockURL = context.mock(URL.class);

        MutableJSONEndpoint<SimpleJSONObject> endpoint = new ConcreteMutableJSONEndpoint(mockURL);

        expectedException.expect(JSONIsNullException.class);
        endpoint.sendRequest();
    }

    @Property
    public void sendJSONHappyPath(
            @From(HTTPRequestMethodGenerator.class) HTTPRequestMethod method,
            @From(JSONTypeGenerator.class) SimpleJSONObject json
    ) throws Exception {
        Mockery context = new Mockery();
        URL mockURL = context.mock(URL.class);
        URLConnection mockConnection = context.mock(URLConnection.class);

        OutputStream mockOutputStream = new ByteArrayOutputStream();
        MutableJSONEndpoint<SimpleJSONObject> endpoint = new ConcreteMutableJSONEndpoint(mockURL);
        endpoint.setRequestMethod(method);
        endpoint.setDesiredJSON(json);

        context.checking(new ExpectationsForTest(mockURL, mockConnection, method, mockOutputStream));

        assertTrue(endpoint.canRun());

        endpoint.sendRequest();

        String writtenData = mockOutputStream.toString();

        assertEquals(
                json,
                endpoint.getMapper().readValue(writtenData, json.getClass())
        );

        context.assertIsSatisfied();

    }

    private static final class ExpectationsForTest extends Expectations {
        public ExpectationsForTest(
                URL mockURL, URLConnection mockConnection, HTTPRequestMethod method,
                OutputStream mockOutputStream
        ) throws Exception {
            oneOf(mockURL).openConnection();
            will(returnValue(mockConnection));

            oneOf(mockConnection).setDoOutput(Boolean.TRUE);
            oneOf(mockConnection).setRequestMethod(method);
            oneOf(mockConnection).setRequestProperty("Content-Type", "application/json");
            oneOf(mockConnection).connect();
            oneOf(mockConnection).disconnect();

            oneOf(mockConnection).getOutputStream();
            will(returnValue(mockOutputStream));
        }
    }
}
