package ca.uwaterloo.iqc.topchef.test.unit.endpoints.services_endpoint;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.Service;
import ca.uwaterloo.iqc.topchef.endpoints.Services;
import ca.uwaterloo.iqc.topchef.endpoints.ServicesEndpoint;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import lombok.Getter;
import lombok.Setter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;


/**
 * Contains unit tests for {@link ServicesEndpoint#getServices()}
 */
@RunWith(JUnitQuickcheck.class)
public final class GetServices extends AbstractServicesEndpointTestCase {

    private static final ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core
            .wrapper.ObjectMapper();

    @Property
    public void getServicesHappyPath(
            @From(ServiceListResponseGenerator.class) ServicesEndpoint.ServiceListResponse response
    ) throws Exception {

        Mockery context = new Mockery();

        MockingPackage mocks = new MockingPackage(context);
        mocks.setInputStream(
                new ByteArrayInputStream(mapper.writeValueAsString(response).getBytes())
        );

        context.checking(new ExpectationsForHappyPath(mocks));

        Services serviceEndpoint = new ServicesEndpoint(mocks.getClient());
        List<Service> services = serviceEndpoint.getServices();

        assertResponseIDsEqualServiceIDs(response, services);

        context.assertIsSatisfied();
    }

    private static void assertResponseIDsEqualServiceIDs(ServicesEndpoint.ServiceListResponse response, List<Service>
            services){
        Service service;
        ServicesEndpoint.ServiceData dataPoint;
        for (Integer index = 0; index < services.size(); index++){
            service = services.get(index);
            dataPoint = response.getData().get(index);

            assertEquals(service.getServiceID(), dataPoint.getId());
        }
    }

    private static class MockingPackage {
        @Getter
        private final Client client;

        @Getter
        private final URLResolver resolver;

        @Getter
        private final URL serviceURL;

        @Getter
        private final URLConnection connectionToServices;

        @Getter
        @Setter
        private InputStream inputStream;

        public MockingPackage(Mockery context){
            client = context.mock(Client.class);
            resolver = context.mock(URLResolver.class);
            serviceURL = context.mock(URL.class);
            connectionToServices = context.mock(URLConnection.class);
            inputStream = new ByteArrayInputStream("".getBytes());
        }
    }

    private static abstract class TestExpectations extends Expectations {
        public TestExpectations(
                Client client, URLResolver resolver, URL serviceURL,
                URLConnection mockConnectionToServices
        ) throws Exception {
            allowing(client).getURLResolver();
            will(returnValue(resolver));

            allowing(resolver).getServiceEndpoint(with(any(UUID.class)));
            will(returnValue(serviceURL));

            oneOf(resolver).getServicesEndpoint();
            will(returnValue(serviceURL));

            expectationsForServiceURL(serviceURL);
            expectationsForConnection(mockConnectionToServices);
        }

        protected abstract void expectationsForConnection(URLConnection connection) throws Exception;

        protected abstract void expectationsForServiceURL(URL serviceURL) throws Exception;
    }

    private static final class ExpectationsForHappyPath extends TestExpectations {

        public ExpectationsForHappyPath(MockingPackage mocks) throws Exception {
            super(mocks.getClient(), mocks.getResolver(), mocks.getServiceURL(), mocks.getConnectionToServices());
            oneOf(mocks.getServiceURL()).openConnection();
            will(returnValue(mocks.getConnectionToServices()));

            oneOf(mocks.getConnectionToServices()).getInputStream();
            will(returnValue(mocks.getInputStream()));
        }

        @Override
        public void expectationsForServiceURL(URL serviceURL) throws Exception {

        }

        @Override
        public void expectationsForConnection(URLConnection connection) throws Exception {
            oneOf(connection).connect();
            oneOf(connection).disconnect();

            oneOf(connection).setDoOutput(Boolean.FALSE);
            oneOf(connection).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(connection).setRequestProperty("Content-Type", "application/json");
            oneOf(connection).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));
        }
    }
}
