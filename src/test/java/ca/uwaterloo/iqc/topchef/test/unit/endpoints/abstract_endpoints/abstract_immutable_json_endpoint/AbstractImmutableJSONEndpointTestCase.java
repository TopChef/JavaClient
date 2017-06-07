package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_immutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.AbstractEndpointsTestCase;

/**
 * Base class for unit tests of {@link AbstractImmutableJSONEndpoint}
 */
public abstract class AbstractImmutableJSONEndpointTestCase extends AbstractEndpointsTestCase {
    protected static final class ConcreteImmutableJSONEndpoint extends AbstractImmutableJSONEndpoint {
        public ConcreteImmutableJSONEndpoint(URL url){
            super(url);
        }
    }
}
