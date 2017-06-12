package ca.uwaterloo.iqc.topchef.test.unit.endpoints.job_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Mockery;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint#setResult(Object)}
 */
@RunWith(JUnitQuickcheck.class)
public final class SetResult extends AbstractJobEndpointTestCase {
    private final ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper
            .ObjectMapper();

    @Property
    public void setResult(
            @From(GenericResponseGenerator.class)JobEndpoint.ResponseToJobDetailsGetRequest<Object, Object> response,
            @From(UUIDGenerator.class) UUID jobId,
            String results
    ) throws Exception {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);
        mocks.setInputStream(
                new ByteArrayInputStream(mapper.writeValueAsString(response).getBytes())
        );

        context.checking(new ExpectationsForSetParameters(mocks, jobId));

        Job endpoint = new JobEndpoint(mocks.getClient(), jobId);
        endpoint.setResult(results);

        response.getData().setResult(results);

        assertEquals(
                response.getData(),
                mapper.readValue(mocks.getOutputStream().toString(), response.getData().getClass())
        );
    }

    private static final class ExpectationsForSetParameters extends ExpectationsForTestWithPut {
        public ExpectationsForSetParameters(MockPackage mocks, UUID jobId) throws Exception {
            super(mocks, jobId);
        }
    }
}
