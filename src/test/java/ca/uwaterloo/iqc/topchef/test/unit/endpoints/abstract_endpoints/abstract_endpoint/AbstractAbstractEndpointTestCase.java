package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_endpoint;

import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractEndpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.AbstractEndpointsTestCase;

/**
 * Base class for unit tests of {@link AbstractEndpoint}
 */
public abstract class AbstractAbstractEndpointTestCase extends AbstractEndpointsTestCase {

    /**
     * Provides a concrete endpoint to test things
     */
    protected static final class ConcreteEndpoint extends AbstractEndpoint {
        /**
         *
         * @param url The desired URL
         */
        public ConcreteEndpoint(URL url){
            super(url);
        }
    }
}
