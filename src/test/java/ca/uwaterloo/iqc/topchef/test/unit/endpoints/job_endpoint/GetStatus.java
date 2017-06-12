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
 * Contains unit tests for {@link JobEndpoint#getParameters()}
 */
@RunWith(JUnitQuickcheck.class)
public final class GetStatus extends AbstractJobEndpointTestCase {
    private final ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core
            .wrapper.ObjectMapper();

    @Property
    public void getStatus(
        @From(GenericResponseGenerator.class) JobEndpoint.ResponseToJobDetailsGetRequest<Object, Object> response,
        @From(UUIDGenerator.class) UUID jobId
    ) throws Exception {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);
        mocks.setInputStream(new ByteArrayInputStream(
                mapper.writeValueAsString(response).getBytes()
        ));

        context.checking(new ExpectationsForGetStatus(mocks, jobId));

        Job job = new JobEndpoint(mocks.getClient(), jobId);
        assertEquals(
                response.getData().getStatus(),
                job.getStatus().toString()
        );

        context.assertIsSatisfied();
    }

    private static final class ExpectationsForGetStatus extends ExpectationsForTests {
        public ExpectationsForGetStatus(MockPackage mocks, UUID jobID) throws Exception {
            super(mocks, jobID);
        }
    }
}
