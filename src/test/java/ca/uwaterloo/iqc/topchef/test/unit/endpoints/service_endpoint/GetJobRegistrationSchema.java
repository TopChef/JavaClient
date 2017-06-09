package ca.uwaterloo.iqc.topchef.test.unit.endpoints.service_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
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

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link ServiceEndpoint#getJobRegistrationSchema()}
 */
@RunWith(JUnitQuickcheck.class)
public final class GetJobRegistrationSchema extends AbstractServiceEndpointTestCase {
    private static final ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core
            .wrapper.ObjectMapper();

    @Property
    public void getSchema(
            @From(UUIDGenerator.class) UUID serviceID,
            @From(ServiceDataGenerator.class) ServiceEndpoint.ServiceData data
    ) throws Exception {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);
        mocks.setInputStream(new ByteArrayInputStream(mapper.writeValueAsString(data).getBytes()));

        context.checking(new ExpectationsForTest(mocks));

        Service service = new ServiceEndpoint(mocks.getClient(), serviceID);

        assertEquals(data.getJob_registration_schema(), service.getJobRegistrationSchema());

        context.assertIsSatisfied();

    }

    private static final class ExpectationsForTest extends Expectations {
        public ExpectationsForTest(MockPackage mocks) throws Exception {
            allowing(mocks.getClient()).getURLResolver();
            will(returnValue(mocks.getResolver()));

            allowing(mocks.getResolver()).getServiceEndpoint(with(any(UUID.class)));
            will(returnValue(mocks.getServiceURL()));

            oneOf(mocks.getServiceURL()).openConnection();
            will(returnValue(mocks.getConnection()));

            setUpConnection(mocks.getConnection());

            oneOf(mocks.getConnection()).getInputStream();
            will(returnValue(mocks.getInputStream()));
        }

        private void setUpConnection(URLConnection connection) throws Exception {
            oneOf(connection).connect();
            oneOf(connection).close();
            oneOf(connection).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(connection).setRequestProperty("Content-Type", "application/json");
            oneOf(connection).setDoOutput(Boolean.FALSE);
            oneOf(connection).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));
        }
    }
}
