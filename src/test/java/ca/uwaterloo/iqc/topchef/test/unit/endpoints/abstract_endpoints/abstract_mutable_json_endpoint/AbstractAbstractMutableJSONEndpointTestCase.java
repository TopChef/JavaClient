package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_mutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractMutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.test.integration.endpoints.AbstractEndpointModelsTestCase;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.StringGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import lombok.Data;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractMutableJSONEndpoint}
 */
public abstract class AbstractAbstractMutableJSONEndpointTestCase extends AbstractEndpointModelsTestCase {

    /**
     * A simple data type to be marshalled and sent to a mock endpoint
     */
    @Data
    protected static final class SimpleJSONObject {
        private String aString;
    }

    /**
     * A concrete endpoint to which JSON can be sent
     */
    protected static final class ConcreteMutableJSONEndpoint extends AbstractMutableJSONEndpoint<SimpleJSONObject> {

        /**
         * @param url A mock URL to which data will be sent
         */
        public ConcreteMutableJSONEndpoint(URL url){
            super(url);
        }
    }

    /**
     * Generates some JSON to post
     */
    protected static final class JSONTypeGenerator extends Generator<SimpleJSONObject> {
        private static final Generator<String> stringGenerator = new StringGenerator();

        public JSONTypeGenerator(){
            super(SimpleJSONObject.class);
        }

        @Override
        public SimpleJSONObject generate(SourceOfRandomness rng, GenerationStatus status){
            SimpleJSONObject object = new SimpleJSONObject();
            object.setAString(stringGenerator.generate(rng, status));

            return object;
        }
    }

    protected static final class HTTPRequestMethodGenerator extends Generator<HTTPRequestMethod> {
        public HTTPRequestMethodGenerator(){
            super(HTTPRequestMethod.class);
        }

        @Override
        public HTTPRequestMethod generate(SourceOfRandomness rng, GenerationStatus status){
            Integer index = rng.nextInt(0, 3);

            switch (index) {
                case 0:
                    return HTTPRequestMethod.POST;
                case 1:
                    return HTTPRequestMethod.PATCH;
                case 2:
                    return HTTPRequestMethod.PUT;
                case 3:
                    return HTTPRequestMethod.GET;
                default:
                    return HTTPRequestMethod.POST;
            }
        }
    }
}
