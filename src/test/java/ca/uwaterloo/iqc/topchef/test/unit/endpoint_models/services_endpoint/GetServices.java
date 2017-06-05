package ca.uwaterloo.iqc.topchef.test.unit.endpoint_models.services_endpoint;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoint_models.Service;
import ca.uwaterloo.iqc.topchef.endpoint_models.Services;
import ca.uwaterloo.iqc.topchef.endpoint_models.ServicesEndpoint;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.hamcrest.core.IsAnything.any;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

/**
 * Contains unit tests for {@link ServicesEndpoint#getServices()}
 */
public final class GetServices extends AbstractServicesEndpointTestCase {
    private static final Mockery context = new Mockery();

    private static final Client mockClient = context.mock(Client.class);

    private static final URLResolver mockURLResolver = context.mock(URLResolver.class);

    private static final URL servicesEndpoint = context.mock(URL.class);

    private static final URLConnection mockConnection = context.mock(URLConnection.class);

    private static final URL mockServiceEndpoint = context.mock(URL.class, "mockServiceEndpoint");

    private Services serviceEndpoint;

    private InputStream testData;

    private UUID serviceID = UUID.randomUUID();

    @Rule
    public static final ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setServiceEndpoint(){
        context.checking(new ExpectationsForConstruction());
        serviceEndpoint = new ServicesEndpoint(mockClient);
        context.assertIsSatisfied();
    }

    @SuppressWarnings("unchecked")
    public void prepareTestData(){
        JSONObject service = new JSONObject();
        service.put("id", serviceID.toString());

        JSONArray serviceList = new JSONArray();
        serviceList.add(service);

        JSONObject response = new JSONObject();

        response.put("data", serviceList);

        testData = new ByteArrayInputStream(response.toJSONString().getBytes());
    }

    public void prepareBadTestData(){
        String badData = "This is not my beautiful JSON, this is not my beautiful wife";
        testData = new ByteArrayInputStream(badData.getBytes());
    }

    public void prepareBadJSONData(){
        String badJSON = "{\"data\": [\"Hello\"]}";
        testData = new ByteArrayInputStream(badJSON.getBytes());
    }

    @Test
    public void getServicesHappyPath() throws Exception {
        prepareTestData();
        context.checking(new ExpectationsForHappyPath());

        assertThat(serviceEndpoint.getServices(), hasItem(any(Service.class)));

        context.assertIsSatisfied();
    }

    @Test
    public void getServicesParseException() throws Exception {
        prepareBadTestData();
        context.checking(new ExpectationsForConnectionParseException());
        exceptionRule.expect(IOException.class);
        serviceEndpoint.getServices();

        context.assertIsSatisfied();
    }

    @Test
    public void getServicesResponseParsingFailure() throws Exception {
        prepareBadJSONData();
        context.checking(new ExpectationsForJSONParseException());
        exceptionRule.expect(IOException.class);
        serviceEndpoint.getServices();
        context.assertIsSatisfied();
    }

    private final class ExpectationsForConstruction extends Expectations {
        public ExpectationsForConstruction(){
            oneOf(mockClient).getURLResolver();
            will(returnValue(mockURLResolver));

            oneOf(mockURLResolver).getServicesEndpoint();
            will(returnValue(servicesEndpoint));
        }
    }

    private final class ExpectationsForHappyPath extends Expectations {
        public ExpectationsForHappyPath() throws Exception {
            oneOf(mockClient).getURLResolver();
            will(returnValue(mockURLResolver));

            oneOf(servicesEndpoint).openConnection();
            will(returnValue(mockConnection));

            oneOf(mockConnection).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(mockConnection).setDoOutput(Boolean.FALSE);
            oneOf(mockConnection).setRequestProperty("Content-Type", "application/json");

            oneOf(mockConnection).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));

            oneOf(mockConnection).getInputStream();
            will(returnValue(testData));

            oneOf(mockURLResolver).getServiceEndpoint(with(any(UUID.class)));
            will(returnValue(mockServiceEndpoint));

            oneOf(mockConnection).connect();
            oneOf(mockConnection).disconnect();
        }
    }

    private final class ExpectationsForConnectionParseException extends Expectations {
        public ExpectationsForConnectionParseException() throws Exception {
            oneOf(mockClient).getURLResolver();
            will(returnValue(mockURLResolver));

            oneOf(servicesEndpoint).openConnection();
            will(returnValue(mockConnection));

            oneOf(mockConnection).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(mockConnection).setDoOutput(Boolean.FALSE);
            oneOf(mockConnection).setRequestProperty("Content-Type", "application/json");

            oneOf(mockConnection).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));

            oneOf(mockConnection).getInputStream();
            will(returnValue(testData));

            oneOf(mockURLResolver).getServiceEndpoint(with(any(UUID.class)));
            will(returnValue(mockServiceEndpoint));

            oneOf(mockConnection).connect();
            oneOf(mockConnection).disconnect();
        }
    }

    private final class ExpectationsForJSONParseException extends Expectations {
        public ExpectationsForJSONParseException() throws Exception {
            oneOf(servicesEndpoint).openConnection();
            will(returnValue(mockConnection));

            oneOf(mockConnection).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(mockConnection).setDoOutput(Boolean.FALSE);
            oneOf(mockConnection).setRequestProperty("Content-Type", "application/json");

            oneOf(mockConnection).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));

            oneOf(mockConnection).getInputStream();
            will(returnValue(testData));

            oneOf(mockConnection).connect();
            oneOf(mockConnection).disconnect();
        }
    }
}
