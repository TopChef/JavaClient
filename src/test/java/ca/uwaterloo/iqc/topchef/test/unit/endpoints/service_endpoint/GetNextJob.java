package ca.uwaterloo.iqc.topchef.test.unit.endpoints.service_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.Service;
import ca.uwaterloo.iqc.topchef.endpoints.ServiceEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import com.github.dmstocking.optional.java.util.Optional;
import com.github.dmstocking.optional.java.util.function.Consumer;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jetbrains.annotations.Contract;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode.NO_CONTENT;
import static ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Contains unit tests for {@link ServiceEndpoint#getNextJob()}
 */
@RunWith(JUnitQuickcheck.class)
public final class GetNextJob extends AbstractServiceEndpointTestCase {
    private static final ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core
            .wrapper.ObjectMapper();

    @Property
    public void jobDoesNotExist(
            @From(UUIDGenerator.class) UUID id
    ) throws HTTPException, IOException, HTTPConnectionCastException {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);

        context.checking(new ExpectationsForNoJob(mocks));

        Service service = new ServiceEndpoint(mocks.getClient(), id);

        Optional<Job> job = service.getNextJob();
        assertFalse(job.isPresent());
    }

    @Property
    public void jobExists(
        @From(UUIDGenerator.class) UUID serviceID,
        @From(JobsEndpointResponseGenerator.class) ServiceEndpoint.JobsEndpointResponse response
    ) throws HTTPException, IOException, HTTPConnectionCastException {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);
        mocks.setInputStream(new ByteArrayInputStream(mapper.writeValueAsString(response).getBytes()));
        context.checking(new ExpectationsForJob(mocks));
        Service service = new ServiceEndpoint(mocks.getClient(), serviceID);
        Optional<Job> job = service.getNextJob();
        job.ifPresentOrElse(new CheckIfJobMatchesResponse(response), new CheckReasonsForNoJob(response));
        context.assertIsSatisfied();
    }

    private static abstract class ExpectationsForTest extends Expectations {
        public ExpectationsForTest(MockPackage mocks) throws IOException, HTTPConnectionCastException {
            allowing(mocks.getClient()).getURLResolver();
            will(returnValue(mocks.getResolver()));

            oneOf(mocks.getResolver()).getServiceEndpoint(with(any(UUID.class)));
            will(returnValue(mocks.getServiceURL()));

            oneOf(mocks.getResolver()).getQueueEndpointForService(with(any(UUID.class)));
            will(returnValue(mocks.getServiceURL()));

            oneOf(mocks.getServiceURL()).openConnection();
            will(returnValue(mocks.getConnection()));

            oneOf(mocks.getConnection()).setDoOutput(Boolean.FALSE);
            oneOf(mocks.getConnection()).setRequestProperty("Content-Type", "application/json");
            oneOf(mocks.getConnection()).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(mocks.getConnection()).getResponseCode();
            will(returnValue(getResponseCodeToReturn()));

            oneOf(mocks.getConnection()).connect();
            oneOf(mocks.getConnection()).close();
        }

        protected abstract HTTPResponseCode getResponseCodeToReturn();
    }

    private static final class ExpectationsForNoJob extends ExpectationsForTest {
        public ExpectationsForNoJob(MockPackage mocks) throws IOException, HTTPConnectionCastException {
            super(mocks);
        }

        @Contract(pure = true)
        @Override
        protected HTTPResponseCode getResponseCodeToReturn(){
            return NO_CONTENT;
        }
    }

    private static final class ExpectationsForJob extends ExpectationsForTest {
        public ExpectationsForJob(MockPackage mocks) throws IOException, HTTPConnectionCastException {
            super(mocks);
            oneOf(mocks.getConnection()).getInputStream();
            will(returnValue(mocks.getInputStream()));

            allowing(mocks.getResolver()).getJobEndpoint(with(any(UUID.class)));
            will(returnValue(mocks.getServiceURL()));
        }

        @Contract(pure = true)
        @Override
        protected HTTPResponseCode getResponseCodeToReturn(){
            return OK;
        }
    }

    private static final class CheckIfJobMatchesResponse implements Consumer<Job> {
        private final ServiceEndpoint.JobsEndpointResponse response;

        public CheckIfJobMatchesResponse(ServiceEndpoint.JobsEndpointResponse response){
            this.response = response;
        }

        @Override
        public void accept(Job job){
            assertEquals(job.getID().toString(), response.getData().get(0).getId());
        }
    }

    private static final class CheckReasonsForNoJob implements Runnable {
        private final ServiceEndpoint.JobsEndpointResponse response;

        public CheckReasonsForNoJob(ServiceEndpoint.JobsEndpointResponse response){
            this.response = response;
        }

        @Override
        public void run(){
            assertEquals(0, response.getData().size());
        }
    }
}
