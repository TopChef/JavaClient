package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_immutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoints.AbstractImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.test.unit.endpoints.AbstractEndpointModelsTestCase;

/**
 * Base class for unit tests of {@link AbstractImmutableJSONEndpoint}
 */
public abstract class AbstractImmutableJSONEndpointTestCase extends AbstractEndpointModelsTestCase {
    protected static final class ConcreteImmutableJSONEndpoint extends AbstractImmutableJSONEndpoint {
        public ConcreteImmutableJSONEndpoint(URL url){
            super(url);
        }
    }
}
