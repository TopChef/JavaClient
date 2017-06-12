package ca.uwaterloo.iqc.topchef.test.unit.endpoints.service_endpoint;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.ServiceEndpoint;
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
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Base class for unit tests of {@link ca.uwaterloo.iqc.topchef.endpoints.ServiceEndpoint}
 */
public abstract class AbstractServiceEndpointTestCase extends AbstractEndpointsTestCase {
    protected static final class ResponseGenerator extends Generator<ServiceEndpoint.ServicesResponse> {
        private static final ServiceDataGenerator dataGenerator = new ServiceDataGenerator();

        public ResponseGenerator(){
            super(ServiceEndpoint.ServicesResponse.class);
        }

        @Override
        public ServiceEndpoint.ServicesResponse generate(SourceOfRandomness rng, GenerationStatus status){
            ServiceEndpoint.ServicesResponse response = new ServiceEndpoint.ServicesResponse();
            response.setData(dataGenerator.generate(rng, status));
            return response;
        }
    }

    protected static final class ServiceDataGenerator extends Generator<ServiceEndpoint.ServiceData> {
        private static final Generator<String> stringGenerator = new StringGenerator();

        public ServiceDataGenerator(){
            super(ServiceEndpoint.ServiceData.class);
        }

        @Override
        public ServiceEndpoint.ServiceData generate(SourceOfRandomness rng, GenerationStatus status){
            ServiceEndpoint.ServiceData data = new ServiceEndpoint.ServiceData();

            data.setDescription(stringGenerator.generate(rng, status));
            data.setName(stringGenerator.generate(rng, status));
            data.setJob_registration_schema(stringGenerator.generate(rng, status));

            return data;
        }
    }

    protected static final class JobsEndpointResponseGenerator extends Generator<ServiceEndpoint.JobsEndpointResponse> {
        private static final Generator<ServiceEndpoint.JobsEndpointData> jobDataGenerator = new
                JobsEndpointDataGenerator();

        public JobsEndpointResponseGenerator(){
            super(ServiceEndpoint.JobsEndpointResponse.class);
        }

        @Override
        public ServiceEndpoint.JobsEndpointResponse generate(SourceOfRandomness rng, GenerationStatus status){
            Integer size = rng.nextInt(0, 50);

            List<ServiceEndpoint.JobsEndpointData> jobsEndpointDataList = new LinkedList<ServiceEndpoint
                    .JobsEndpointData>();

            for (Integer index = 0; index < size; index++){
                jobsEndpointDataList.add(jobDataGenerator.generate(rng, status));
            }

            ServiceEndpoint.JobsEndpointResponse response = new ServiceEndpoint.JobsEndpointResponse();
            response.setMeta("");
            response.setData(jobsEndpointDataList);

            return response;
        }
    }

    protected static final class JobsEndpointDataGenerator extends Generator<ServiceEndpoint.JobsEndpointData> {
        private static final Generator<UUID> uuidGenerator = new UUIDGenerator();
        private static final Generator<Date> dateGenerator = new DateGenerator();

        public JobsEndpointDataGenerator(){
            super(ServiceEndpoint.JobsEndpointData.class);
        }

        @Override
        public ServiceEndpoint.JobsEndpointData generate(SourceOfRandomness rng, GenerationStatus status){
            ServiceEndpoint.JobsEndpointData data = new ServiceEndpoint.JobsEndpointData();

            data.setId(uuidGenerator.generate(rng, status).toString());
            data.setDate_submitted(dateGenerator.generate(rng, status).toString());
            data.setStatus("REGISTERED");

            return data;
        }
    }

    protected static class MockPackage {
        @Getter
        private final Client client;

        @Getter
        private final URLResolver resolver;

        @Getter
        private final URL serviceURL;

        @Getter
        private final URLConnection connection;

        @Getter
        @Setter
        private InputStream inputStream;

        public MockPackage(Mockery context){
            client = context.mock(Client.class);
            resolver = context.mock(URLResolver.class);
            serviceURL = context.mock(URL.class);
            connection = context.mock(URLConnection.class);
            inputStream = new ByteArrayInputStream("".getBytes());
        }
    }
}
