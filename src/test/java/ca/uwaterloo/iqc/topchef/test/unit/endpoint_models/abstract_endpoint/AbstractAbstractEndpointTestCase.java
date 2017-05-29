package ca.uwaterloo.iqc.topchef.unit.endpoint_models.abstract_endpoint;

import ca.uwaterloo.iqc.topchef.endpoint_models.AbstractEndpoint;
import ca.uwaterloo.iqc.topchef.unit.endpoint_models.AbstractEndpointModelsTestCase;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Base class for unit tests of {@link ca.uwaterloo.iqc.topchef.endpoint_models.AbstractEndpoint}
 */
public abstract class AbstractAbstractEndpointTestCase extends AbstractEndpointModelsTestCase {

    /**
     * Provides a concrete endpoint to test things
     */
    protected class ConcreteEndpoint extends AbstractEndpoint {
        /**
         *
         * @param url The desired URL
         */
        public ConcreteEndpoint(URL url){
            super(url);
        }

        /**
         * @param url The desired URL
         * @throws MalformedURLException if the URL is bad
         */
        public ConcreteEndpoint(String url) throws MalformedURLException {
            super(url);
        }
    }
}
