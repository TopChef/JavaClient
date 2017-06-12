package ca.uwaterloo.iqc.topchef.test.unit.endpoints.job_endpoint;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint;
import ca.uwaterloo.iqc.topchef.test.unit.endpoints.AbstractEndpointsTestCase;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.StringGenerator;
import com.pholser.junit.quickcheck.generator.java.util.DateGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import lombok.Getter;
import lombok.Setter;
import org.jmock.Mockery;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

        public MockPackage(Mockery context){
            client = context.mock(Client.class);
            resolver = context.mock(URLResolver.class);
            url = context.mock(URL.class);
            connection = context.mock(URLConnection.class);
            inputStream = new ByteArrayInputStream("".getBytes());
        }
    }

    protected static final class JobStatusGenerator extends Generator<Job.Status> {
        public JobStatusGenerator(){
            super(Job.Status.class);
        }

        @Override
        public Job.Status generate(SourceOfRandomness rng, GenerationStatus status){
            return Job.Status.values()[rng.nextInt(0, Job.Status.values().length - 1)];
        }
    }

    protected static abstract class JobDetailsGenerator<P, R> extends Generator<JobEndpoint.JobDetails>{
        private final Generator<P> jobParametersGenerator;
        private final Generator<R> jobResultsGenerator;

        private static final Generator<Date> dateGenerator = new DateGenerator();
        private static final Generator<UUID> uuidGenerator = new UUIDGenerator();
        private static final Generator<Job.Status> statusGenerator = new JobStatusGenerator();

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
            details.setDate_submitted(dateGenerator.generate(rng, status));
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
}
