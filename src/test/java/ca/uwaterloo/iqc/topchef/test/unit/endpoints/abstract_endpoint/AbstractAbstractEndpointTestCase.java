package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoint;

import ca.uwaterloo.iqc.topchef.test.unit.endpoints.AbstractEndpointModelsTestCase;
import ca.uwaterloo.iqc.topchef.endpoints.AbstractEndpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;

/**
 * Base class for unit tests of {@link ca.uwaterloo.iqc.topchef.endpoints.AbstractEndpoint}
 */
public abstract class AbstractAbstractEndpointTestCase extends AbstractEndpointModelsTestCase {

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
