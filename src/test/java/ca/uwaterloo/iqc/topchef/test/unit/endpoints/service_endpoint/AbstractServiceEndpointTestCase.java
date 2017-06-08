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
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import lombok.Getter;
import lombok.Setter;
import org.jmock.Mockery;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Base class for unit tests of {@link ca.uwaterloo.iqc.topchef.endpoints.ServiceEndpoint}
 */
public abstract class AbstractServiceEndpointTestCase extends AbstractEndpointsTestCase {
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
