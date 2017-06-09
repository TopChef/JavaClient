package ca.uwaterloo.iqc.topchef.test.unit.endpoints.service_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.Service;
import ca.uwaterloo.iqc.topchef.endpoints.ServiceEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit test for {@link ServiceEndpoint#getJobs()}
 */
@RunWith(JUnitQuickcheck.class)
public final class GetJobs extends AbstractServiceEndpointTestCase {
    private static final ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core
            .wrapper.ObjectMapper();

    @Property
    public void getJobsHappyPath(
            @From(JobsEndpointResponseGenerator.class) ServiceEndpoint.JobsEndpointResponse response,
            @From(UUIDGenerator.class) UUID uuid
    ) throws HTTPException, IOException, HTTPConnectionCastException {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);

        mocks.setInputStream(new ByteArrayInputStream(
            mapper.writeValueAsString(response).getBytes()
        ));

        context.checking(new ExpectationsForHappyPath(mocks));

        Service service = new ServiceEndpoint(mocks.getClient(), uuid);
        List<Job> jobs = service.getJobs();

        assertJobIdsEqual(jobs, response);
        context.assertIsSatisfied();
    }

    private static void assertJobIdsEqual(List<Job> jobs, ServiceEndpoint.JobsEndpointResponse response){
        assertEquals(jobs.size(), response.getData().size());

        List<ServiceEndpoint.JobsEndpointData> data = response.getData();

        for (Integer index = 0; index < jobs.size(); index++){
            assertEquals(
                    jobs.get(0).getID().toString(),
                    data.get(0).getId()
            );
        }
    }

    private static final class ExpectationsForHappyPath extends Expectations {
        public ExpectationsForHappyPath(MockPackage mocks) throws HTTPConnectionCastException, IOException {
            allowing(mocks.getClient()).getURLResolver();
            will(returnValue(mocks.getResolver()));

            oneOf(mocks.getResolver()).getServiceEndpoint(with(any(UUID.class)));
            will(returnValue(mocks.getServiceURL()));

            oneOf(mocks.getResolver()).getJobsEndpointForService(with(any(UUID.class)));
            will(returnValue(mocks.getServiceURL()));

            allowing(mocks.getResolver()).getJobEndpoint(with(any(UUID.class)));
            will(returnValue(mocks.getServiceURL()));

            oneOf(mocks.getServiceURL()).openConnection();
            will(returnValue(mocks.getConnection()));

            oneOf(mocks.getConnection()).setDoOutput(Boolean.FALSE);
            oneOf(mocks.getConnection()).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(mocks.getConnection()).setRequestProperty("Content-Type", "application/json");
            oneOf(mocks.getConnection()).getInputStream();
            will(returnValue(mocks.getInputStream()));

            oneOf(mocks.getConnection()).connect();
            oneOf(mocks.getConnection()).close();
            oneOf(mocks.getConnection()).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));
        }
    }
}
