package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_mutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractMutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.MutableJSONEndpoint;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Mockery;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for
 * {@link AbstractMutableJSONEndpoint#getRequestMethod()}
 * and
 * {@link AbstractMutableJSONEndpoint#setRequestMethod(HTTPRequestMethod)}
 *
 * I'll keep them in one quickcheck property for now
 */
@RunWith(JUnitQuickcheck.class)
public final class GetAndSetRequestMethod extends AbstractAbstractMutableJSONEndpointTestCase {
    @Property
    public void getRequestMethod(
            @From(HTTPRequestMethodGenerator.class) HTTPRequestMethod method
            ){
        Mockery context = new Mockery();
        URL mockURL = context.mock(URL.class);

        MutableJSONEndpoint endpoint = new ConcreteMutableJSONEndpoint(mockURL);

        endpoint.setRequestMethod(method);
        assertEquals(method, endpoint.getRequestMethod());
    }
}
