package ca.uwaterloo.iqc.topchef.test.unit.endpoints.services_endpoint;

import ca.uwaterloo.iqc.topchef.endpoints.ServicesEndpoint;
import ca.uwaterloo.iqc.topchef.test.unit.endpoints.AbstractEndpointModelsTestCase;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.StringGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Base class for unit tests of {@link ca.uwaterloo.iqc.topchef.endpoints.ServicesEndpoint}
 */
public abstract class AbstractServicesEndpointTestCase extends AbstractEndpointModelsTestCase {

    protected static final class ServiceDataGenerator extends Generator<ServicesEndpoint.ServiceData> {
        private static final Generator<String> nameGenerator = new StringGenerator();
        private static final Generator<UUID> serviceIDGenerator = new UUIDGenerator();
        private static final Generator<String> urlGenerator = new URLStringGenerator();

        public ServiceDataGenerator(){
            super(ServicesEndpoint.ServiceData.class);
        }

        @Override
        public ServicesEndpoint.ServiceData generate(SourceOfRandomness rng, GenerationStatus status){
            ServicesEndpoint.ServiceData service = new ServicesEndpoint.ServiceData();

            service.setId(serviceIDGenerator.generate(rng, status));
            service.setName(nameGenerator.generate(rng, status));
            service.setHas_timed_out(rng.nextBoolean());
            service.setUrl(urlGenerator.generate(rng, status));

            return service;
        }
    }

    protected static final class ServiceListResponseGenerator extends Generator<ServicesEndpoint.ServiceListResponse> {
        private static final Generator<ServicesEndpoint.ServiceData> serviceGenerator = new ServiceDataGenerator();

        public ServiceListResponseGenerator(){
            super(ServicesEndpoint.ServiceListResponse.class);
        }

        @Override
        public ServicesEndpoint.ServiceListResponse generate(SourceOfRandomness rng, GenerationStatus status){
            return generate(rng, status, 0, 50);
        }

        public ServicesEndpoint.ServiceListResponse generate(SourceOfRandomness rng, GenerationStatus status, Integer
                minimumServiceNumber, Integer maximumServiceNumber){
            Integer numberOfServices = rng.nextInt(minimumServiceNumber, maximumServiceNumber);

            List<ServicesEndpoint.ServiceData> serviceList = new LinkedList<ServicesEndpoint.ServiceData>();

            for (Integer index = 0; index < numberOfServices; index++){
                serviceList.add(serviceGenerator.generate(rng, status));
            }

            ServicesEndpoint.ServiceListResponse response = new ServicesEndpoint.ServiceListResponse();
            response.setData(serviceList);
            response.setMeta("");

            return response;
        }
    }
}
