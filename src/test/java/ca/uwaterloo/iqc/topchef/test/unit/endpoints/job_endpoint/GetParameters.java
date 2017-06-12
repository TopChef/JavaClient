package ca.uwaterloo.iqc.topchef.test.unit.endpoints.job_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
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
 * Contains unit tests for {@link JobEndpoint#getParameters()}
 */
@RunWith(JUnitQuickcheck.class)
public final class GetParameters extends AbstractJobEndpointTestCase {

    private static final ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core
            .wrapper.ObjectMapper();

    @Property
    public void getParameters(
            @From(GenericResponseGenerator.class) JobEndpoint.ResponseToJobDetailsGetRequest<Object, Object> response,
            @From(UUIDGenerator.class) UUID jobId
    ) throws Exception {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);

        mocks.setInputStream(new ByteArrayInputStream(mapper.writeValueAsString(response).getBytes()));

        context.checking(new ExpectationsForGetParameters(mocks, jobId));

        Job job = new JobEndpoint(mocks.getClient(), jobId);

        assertEquals(response.getData().getParameters(), job.getParameters());
        context.assertIsSatisfied();
    }

    private static final class ExpectationsForGetParameters extends Expectations {
        public ExpectationsForGetParameters(MockPackage mocks, UUID jobID) throws Exception {
            oneOf(mocks.getClient()).getURLResolver();
            will(returnValue(mocks.getResolver()));

            oneOf(mocks.getResolver()).getJobEndpoint(jobID);
            will(returnValue(mocks.getUrl()));

            oneOf(mocks.getUrl()).openConnection();
            will(returnValue(mocks.getConnection()));

            oneOf(mocks.getConnection()).setDoOutput(Boolean.FALSE);
            oneOf(mocks.getConnection()).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(mocks.getConnection()).setRequestProperty("Content-Type", "application/json");
            oneOf(mocks.getConnection()).connect();
            oneOf(mocks.getConnection()).close();

            oneOf(mocks.getConnection()).getInputStream();
            will(returnValue(mocks.getInputStream()));

            oneOf(mocks.getConnection()).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));
        }
    }
}
