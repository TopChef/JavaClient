package ca.uwaterloo.iqc.topchef.test.unit.endpoints.job_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint;
import ca.uwaterloo.iqc.topchef.test.unit.AbstractUnitTestCase;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Mockery;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link JobEndpoint#getResult()}
 */
@RunWith(JUnitQuickcheck.class)
public final class GetResult extends AbstractJobEndpointTestCase {

    private final ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core
            .wrapper.ObjectMapper();

    @Property
    public void getResult(
            @From(GenericResponseGenerator.class) JobEndpoint.ResponseToJobDetailsGetRequest<Object, Object> response,
            @From(UUIDGenerator.class) UUID jobId
    ) throws Exception {
        Mockery context = new Mockery();
        AbstractJobEndpointTestCase.MockPackage mocks = new AbstractJobEndpointTestCase.MockPackage(context);

        mocks.setInputStream(new ByteArrayInputStream(mapper.writeValueAsString(response).getBytes()));

        context.checking(new ExpectationsForGetResult(mocks, jobId));

        Job job = new JobEndpoint(mocks.getClient(), jobId);

        assertEquals(response.getData().getResult(), job.getResult());
        context.assertIsSatisfied();
    }

    private static final class ExpectationsForGetResult extends AbstractJobEndpointTestCase.ExpectationsForTests {
        public ExpectationsForGetResult(MockPackage mocks, UUID id) throws Exception {
            super(mocks, id);
        }
    }
}
