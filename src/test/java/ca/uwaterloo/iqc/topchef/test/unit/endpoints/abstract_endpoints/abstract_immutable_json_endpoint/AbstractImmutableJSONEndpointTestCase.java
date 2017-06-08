package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_immutable_json_endpoint;

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
}
