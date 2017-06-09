package ca.uwaterloo.iqc.topchef.test.unit.endpoints.service_endpoint;

import ca.uwaterloo.iqc.topchef.endpoints.Service;
import ca.uwaterloo.iqc.topchef.endpoints.ServiceEndpoint;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for
 * {@link ca.uwaterloo.iqc.topchef.endpoints.ServiceEndpoint#ServiceEndpoint(Client, String)}
 */
@RunWith(JUnitQuickcheck.class)
public final class StringConstructor extends AbstractServiceEndpointTestCase {
    @Property
    public void canBuildUsingString(
        @From(UUIDGenerator.class) UUID uuid
    ){
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);
        context.checking(new ExpectationsForTest(mocks));
        Service endpoint = new ServiceEndpoint(mocks.getClient(), uuid.toString());
        assertEquals(uuid, endpoint.getServiceID());
        context.assertIsSatisfied();
    }

    private static final class ExpectationsForTest extends Expectations {
        public ExpectationsForTest(MockPackage mocks){
            oneOf(mocks.getClient()).getURLResolver();
            will(returnValue(mocks.getResolver()));

            oneOf(mocks.getResolver()).getServiceEndpoint(with(any(UUID.class)));
            will(returnValue(mocks.getServiceURL()));
        }
    }
}
