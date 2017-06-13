package ca.uwaterloo.iqc.topchef.test.unit.endpoints.job_endpoint;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint;
import ca.uwaterloo.iqc.topchef.endpoints.JobStatus;
import ca.uwaterloo.iqc.topchef.test.unit.endpoints.AbstractEndpointsTestCase;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.StringGenerator;
import com.pholser.junit.quickcheck.generator.java.util.DateGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import lombok.Getter;
import lombok.Setter;
import org.jmock.Expectations;
import org.jmock.Mockery;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

/**
 * Base class for unit tests of {@link ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint}
 */
public abstract class AbstractJobEndpointTestCase extends AbstractEndpointsTestCase {

    protected static final class MockPackage {
        @Getter
        private Client client;

        @Getter
        private URLResolver resolver;

        @Getter
        private URL url;

        @Getter
        private URLConnection connection;

        @Getter
        @Setter
        private InputStream inputStream;

        @Getter
        @Setter
        private OutputStream outputStream;

        public MockPackage(Mockery context){
            client = context.mock(Client.class);
            resolver = context.mock(URLResolver.class);
            url = context.mock(URL.class);
            connection = context.mock(URLConnection.class);
            inputStream = new ByteArrayInputStream("".getBytes());
            outputStream = new ByteArrayOutputStream();
        }
    }

    protected static final class JobStatusGenerator extends Generator<JobStatus> {
        public JobStatusGenerator(){
            super(JobStatus.class);
        }

        @Override
        public JobStatus generate(SourceOfRandomness rng, GenerationStatus status){
            return JobStatus.values()[rng.nextInt(0, JobStatus.values().length - 1)];
        }
    }

    protected static abstract class JobDetailsGenerator<P, R> extends Generator<JobEndpoint.JobDetails>{
        private final Generator<P> jobParametersGenerator;
        private final Generator<R> jobResultsGenerator;

        private static final Generator<Date> dateGenerator = new DateGenerator();
        private static final Generator<UUID> uuidGenerator = new UUIDGenerator();
        private static final Generator<JobStatus> statusGenerator = new JobStatusGenerator();

        public JobDetailsGenerator(Generator<P> jobParametersGenerator, Generator<R> jobResultsGenerator){
            super(JobEndpoint.JobDetails.class);
            this.jobParametersGenerator = jobParametersGenerator;
            this.jobResultsGenerator = jobResultsGenerator;
        }

        @Override
        public JobEndpoint.JobDetails<P, R> generate(SourceOfRandomness rng, GenerationStatus status){
            JobEndpoint.JobDetails<P, R> details = new JobEndpoint.JobDetails<P, R>();
            details.setParameters(jobParametersGenerator.generate(rng, status));
            details.setResult(jobResultsGenerator.generate(rng, status));
            details.setDate_submitted(dateGenerator.generate(rng, status).toString());
            details.setId(uuidGenerator.generate(rng, status).toString());
            details.setStatus(statusGenerator.generate(rng, status).toString());

            return details;
        }
    }

    protected static final class GenericJobDetailsGenerator extends JobDetailsGenerator<String, String>{
        private static final Generator<String> stringGenerator = new StringGenerator();

        public GenericJobDetailsGenerator(){
            super(stringGenerator, stringGenerator);
        }
    }

    protected static abstract class ResponseToJobDetailsRequestGenerator<P, R> extends Generator<JobEndpoint
            .ResponseToJobDetailsGetRequest> {

        private final JobDetailsGenerator<P, R> jobDetailsGenerator;

        public ResponseToJobDetailsRequestGenerator(
                JobDetailsGenerator<P, R> jobDetailsGenerator
        ){
            super(JobEndpoint.ResponseToJobDetailsGetRequest.class);
            this.jobDetailsGenerator = jobDetailsGenerator;
        }

        @Override
        public JobEndpoint.ResponseToJobDetailsGetRequest<P, R> generate(
                SourceOfRandomness rng, GenerationStatus status
        ){
            JobEndpoint.ResponseToJobDetailsGetRequest<P, R> response = new JobEndpoint
                    .ResponseToJobDetailsGetRequest<P, R>();
            JobEndpoint.JobDetails<P, R> details = jobDetailsGenerator.generate(rng, status);
            response.setData(details);

            return response;
        }
    }

    protected static final class GenericResponseGenerator extends ResponseToJobDetailsRequestGenerator<String, String>{
        private static final JobDetailsGenerator<String, String> jobDetailsGenerator = new GenericJobDetailsGenerator();

        public GenericResponseGenerator(){
            super(jobDetailsGenerator);
        }
    }

    protected static abstract class ExpectationsForTests extends Expectations {
        public ExpectationsForTests(MockPackage mocks, UUID jobID) throws Exception {
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

    protected static abstract class ExpectationsForTestWithPut extends ExpectationsForTests {
        public ExpectationsForTestWithPut(MockPackage mocks, UUID jobID) throws Exception {
            super(mocks, jobID);

            allowing(mocks.getUrl()).openConnection();
            will(returnValue(mocks.getConnection()));

            allowing(mocks.getConnection()).setRequestMethod(with(any(HTTPRequestMethod.class)));
            allowing(mocks.getConnection()).setRequestProperty("Content-Type", "application/json");
            allowing(mocks.getConnection()).setDoOutput(with(any(Boolean.class)));

            allowing(mocks.getConnection()).getInputStream();
            will(returnValue(mocks.getInputStream()));

            allowing(mocks.getConnection()).getOutputStream();
            will(returnValue(mocks.getOutputStream()));

            allowing(mocks.getConnection()).connect();
            allowing(mocks.getConnection()).close();

            allowing(mocks.getConnection()).getResponseCode();
            will(returnValue(HTTPResponseCode.OK));
        }
    }
}
