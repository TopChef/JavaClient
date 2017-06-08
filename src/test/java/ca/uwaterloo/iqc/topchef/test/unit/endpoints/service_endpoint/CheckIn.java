package ca.uwaterloo.iqc.topchef.test.unit.endpoints.service_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.Service;
import ca.uwaterloo.iqc.topchef.endpoints.ServiceEndpoint;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.fail;

/**
 * Contains unit tests for {@link ServiceEndpoint#checkIn()}
 */
@RunWith(JUnitQuickcheck.class)
public final class CheckIn extends AbstractServiceEndpointTestCase {
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

    private static final class ExpectationsForHappyPath extends Expectations {
        public ExpectationsForHappyPath(MockPackage mocks) throws Exception {
            oneOf(mocks.getClient()).getURLResolver();
            will(returnValue(mocks.getResolver()));

            oneOf(mocks.getResolver()).getServiceEndpoint(with(any(UUID.class)));
            will(returnValue(mocks.getServiceURL()));

            oneOf(mocks.getServiceURL()).openConnection();
            will(returnValue(mocks.getConnection()));

            configureConnection(mocks.getConnection());
        }

        private void configureConnection(URLConnection connection) throws Exception {
            oneOf(connection).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));

            oneOf(connection).connect();
            oneOf(connection).disconnect();
            oneOf(connection).setRequestMethod(HTTPRequestMethod.PATCH);
            oneOf(connection).setDoOutput(Boolean.FALSE);
        }
    }
}
