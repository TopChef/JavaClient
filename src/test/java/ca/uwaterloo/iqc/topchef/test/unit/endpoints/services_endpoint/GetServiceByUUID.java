package ca.uwaterloo.iqc.topchef.test.unit.endpoints.services_endpoint;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.Service;
import ca.uwaterloo.iqc.topchef.endpoints.Services;
import ca.uwaterloo.iqc.topchef.endpoints.ServicesEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.ServiceNotFoundException;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoints.ServicesEndpoint#getServiceByUUID(UUID)}
 */
@RunWith(JUnitQuickcheck.class)
public class GetServiceByUUID extends AbstractServicesEndpointTestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Property
    public void serviceExists(
            @From(UUIDGenerator.class) UUID uuid
    ) throws Exception {
        Mockery context = new Mockery();
        MockingPackage mocks = new MockingPackage(context);
        context.checking(new TrueTestExpectations(mocks));
        Services services = new ServicesEndpoint(mocks.getClient());

        Service service = services.getServiceByUUID(uuid);

        assertEquals(uuid, service.getServiceID());

        context.assertIsSatisfied();
    }

    @Property
    public void serviceDoesNotExist(
        @From(UUIDGenerator.class) UUID uuid
    ) throws Exception {
        Mockery context = new Mockery();
        MockingPackage mocks = new MockingPackage(context);
        context.checking(new ExpectationsForServiceNotFound(mocks));
        Services services = new ServicesEndpoint(mocks.getClient());

        expectedException.expect(ServiceNotFoundException.class);
        services.getServiceByUUID(uuid);

        context.assertIsSatisfied();
    }

    private final class MockingPackage {
        @Getter
        private Client client;

        @Getter
        private URLResolver resolver;

        @Getter
        private URLConnection connection;

        @Getter
        private URL servicesEndpoint;

        @Getter
        private URL serviceEndpoint;

        public MockingPackage(Mockery context){
            client = context.mock(Client.class);
            resolver = context.mock(URLResolver.class);
            servicesEndpoint = context.mock(URL.class);
            connection = context.mock(URLConnection.class);
            serviceEndpoint = context.mock(URL.class, "serviceEndpoint");
        }
    }

    private abstract class TestExpectations extends Expectations {
        public TestExpectations(MockingPackage mocks) throws Exception {
            allowing(mocks.getClient()).getURLResolver();
            will(returnValue(mocks.getResolver()));

            oneOf(mocks.getResolver()).getServicesEndpoint();
            will(returnValue(mocks.getServicesEndpoint()));

            oneOf(mocks.getResolver()).getServiceEndpoint(with(any(UUID.class)));
            will(returnValue(mocks.getServiceEndpoint()));

            oneOf(mocks.getServiceEndpoint()).openConnection();
            will(returnValue(mocks.getConnection()));

            oneOf(mocks.getConnection()).getResponseCode();
            will(returnValue(getResponseCode()));

            oneOf(mocks.getConnection()).setRequestMethod(HTTPRequestMethod.GET);
        }

        protected abstract HTTPResponseCode getResponseCode();
    }

    private final class TrueTestExpectations extends TestExpectations {
        public TrueTestExpectations(MockingPackage mocks) throws Exception {
            super(mocks);
        }

        @Contract(pure = true)
        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.OK;
        }
    }

    private final class ExpectationsForServiceNotFound extends TestExpectations {
        public ExpectationsForServiceNotFound(MockingPackage mocks) throws Exception {
            super(mocks);
        }

        @Contract(pure = true)
        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.NOT_FOUND;
        }
    }
}
