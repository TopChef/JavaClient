package ca.uwaterloo.iqc.topchef.test.unit.endpoints.service_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.Service;
import ca.uwaterloo.iqc.topchef.endpoints.ServiceEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.java.util.RFC4122;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.fail;

/**
 * Contains unit tests for {@link ServiceEndpoint#checkIn()}
 */
@RunWith(JUnitQuickcheck.class)
public final class CheckIn extends AbstractServiceEndpointTestCase {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Property
    public void checkInHappyPath(
            @From(UUIDGenerator.class) UUID uuid
    ) throws Exception {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);

        context.checking(new ExpectationsForHappyPath(mocks));

        Service service = new ServiceEndpoint(mocks.getClient(), uuid);

        try {
            service.checkIn();
        } catch (Exception error){
            fail("Check-in for Happy path should have run without error");
        }
    }

    @Property
    public void connectionError(
            @From(RFC4122.Version4.class) UUID uuid
    ) throws Exception {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);

        context.checking(new ExpectationsForConnectionError(mocks));

        Service service = new ServiceEndpoint(mocks.getClient(), uuid);

        expectedException.expect(IOException.class);

        service.checkIn();

        context.assertIsSatisfied();
    }

    private static abstract class TestExpectations extends Expectations {
        public TestExpectations(MockPackage mocks){
            super();
            oneOf(mocks.getClient()).getURLResolver();
            will(returnValue(mocks.getResolver()));

            oneOf(mocks.getResolver()).getServiceEndpoint(with(any(UUID.class)));
            will(returnValue(mocks.getServiceURL()));
        }
    }

    private static final class ExpectationsForHappyPath extends TestExpectations {
        public ExpectationsForHappyPath(MockPackage mocks) throws Exception {
            super(mocks);

            oneOf(mocks.getServiceURL()).openConnection();
            will(returnValue(mocks.getConnection()));

            configureConnection(mocks.getConnection());
        }

        private void configureConnection(URLConnection connection) throws Exception {
            oneOf(connection).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));

            oneOf(connection).connect();
            oneOf(connection).close();
            oneOf(connection).setRequestMethod(HTTPRequestMethod.PATCH);
            oneOf(connection).setDoOutput(Boolean.FALSE);
        }
    }

    private static final class ExpectationsForConnectionError extends TestExpectations {
        public ExpectationsForConnectionError(MockPackage mocks) throws Exception {
            super(mocks);
            oneOf(mocks.getServiceURL()).openConnection();
            will(throwException(new HTTPConnectionCastException("Kaboom")));
        }
    }
}
