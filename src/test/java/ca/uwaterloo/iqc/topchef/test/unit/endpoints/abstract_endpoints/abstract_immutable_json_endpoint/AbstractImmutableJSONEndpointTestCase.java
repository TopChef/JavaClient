package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_immutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.AbstractEndpointsTestCase;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.StringGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;

/**
 * Base class for unit tests of {@link AbstractImmutableJSONEndpoint}
 */
public abstract class AbstractImmutableJSONEndpointTestCase extends AbstractEndpointsTestCase {
    protected static final class ConcreteImmutableJSONEndpoint extends AbstractImmutableJSONEndpoint {
        public ConcreteImmutableJSONEndpoint(URL url){
            super(url);
        }
    }

    /**
     * A type to marshall to JSON
     */
    @EqualsAndHashCode
    protected static final class JSON {

        /**
         * A random string of characters
         */
        @Getter
        @Setter
        private String data;
    }

    /**
     * A generator for making JSON
     */
    protected static final class JSONGenerator extends Generator<JSON> {
        /**
         * A generator for strings
         */
        private final Generator<String> stringGenerator = new StringGenerator();

        /**
         * Create a generator
         */
        public JSONGenerator(){
            super(JSON.class);
        }

        /**
         *
         * @param rng A source of randomness
         * @param status The generation status
         * @return A randomly-generated JSON type
         */
        @Override
        public JSON generate(SourceOfRandomness rng, GenerationStatus status){
            JSON json = new JSON();
            json.setData(stringGenerator.generate(rng, status));
            return json;
        }
    }

    protected static final class ObjectMapperGenerator extends Generator<ObjectMapper> {

        public ObjectMapperGenerator(){
            super(ObjectMapper.class);
        }

        /**
         * A generator for JSON object mappers
         */
        @Contract("_, _ -> !null")
        @Override
        public ObjectMapper generate(SourceOfRandomness rng, GenerationStatus status){
            return new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper();
        }
    }

    /**
     * Generates random HTTP response codes
     */
    protected static final class HTTPResponseCodeGenerator extends Generator<HTTPResponseCode> {
        public HTTPResponseCodeGenerator(){
            super(HTTPResponseCode.class);
        }

        /**
         *
         * @param rng The random number generator provided by Quickcheck
         * @param status THe generation status
         * @return A random HTTP response code drawn from {@link HTTPResponseCode}
         */
        @Override
        public HTTPResponseCode generate(SourceOfRandomness rng, GenerationStatus status){
            return HTTPResponseCode.values()[rng.nextInt(0, HTTPResponseCode.values().length - 1)];
        }
    }
}
