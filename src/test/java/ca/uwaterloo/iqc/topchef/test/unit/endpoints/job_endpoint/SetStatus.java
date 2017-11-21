package ca.uwaterloo.iqc.topchef.test.unit.endpoints.job_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint;
import ca.uwaterloo.iqc.topchef.endpoints.JobStatus;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Mockery;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint#setStatus(JobStatus)}
 */
@RunWith(JUnitQuickcheck.class)
public final class SetStatus extends AbstractJobEndpointTestCase {
    /**
     * The JSON mapper used to map job data to JSON
     */
    private final ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper
            .ObjectMapper();

    /**
     *
     * @param response
     * @param jobId
     * @param newStatus
     * @throws Exception
     */
    @Property
    public void setStatus(
            @From(GenericResponseGenerator.class)JobEndpoint.ResponseToJobDetailsGetRequest<Object, Object> response,
            @From(UUIDGenerator.class) UUID jobId,
            @From(JobStatusGenerator.class) JobStatus newStatus
    ) throws Exception {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);
        mocks.setInputStream(
            new ByteArrayInputStream(mapper.writeValueAsString(response).getBytes())
        );

        context.checking(new ExpectationsForSetStatus(mocks, jobId));

        Job endpoint = new JobEndpoint(mocks.getClient(), jobId);
        endpoint.setStatus(newStatus);

        response.getData().setStatus(newStatus.toString().toUpperCase());

        assertEquals(
            response.getData(),
            mapper.readValue(mocks.getOutputStream().toString(), response.getData().getClass())
        );
    }

    private static final class ExpectationsForSetStatus extends ExpectationsForTestWithPut {
        public ExpectationsForSetStatus(MockPackage mocks, UUID jobId) throws Exception {
            super(mocks, jobId);
        }
    }
}
