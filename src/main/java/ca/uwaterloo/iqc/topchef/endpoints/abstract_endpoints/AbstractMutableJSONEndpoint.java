package ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;

/**
 * Base class for endpoints where JSON can be mutated
 */
public abstract class AbstractMutableJSONEndpoint extends AbstractImmutableJSONEndpoint implements MutableJSONEndpoint {
    public AbstractMutableJSONEndpoint(URL url){
        super(url);
    }
}
