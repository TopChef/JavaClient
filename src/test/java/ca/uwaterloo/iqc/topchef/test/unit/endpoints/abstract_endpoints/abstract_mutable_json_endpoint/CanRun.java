package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_mutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractMutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.MutableJSONEndpoint;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Contains unit tests for
 * {@link AbstractMutableJSONEndpoint#canRun()}
 */
@RunWith(JUnitQuickcheck.class)
public final class CanRun extends AbstractAbstractMutableJSONEndpointTestCase {

    /**
     * Tests that a concrete endpoint cannot run just after construction
     */
    @Test
    public void cannotRunByDefault() {
        Mockery context = new Mockery();
        URL mockURL = context.mock(URL.class);

        ConcreteMutableJSONEndpoint endpoint = new ConcreteMutableJSONEndpoint(mockURL);
        assertFalse(endpoint.canRun());
    }

    @Property
    public void canRunWhenJSONIsSet(
            @From(JSONTypeGenerator.class) SimpleJSONObject json
    ){
        Mockery context = new Mockery();
        URL mockURL = context.mock(URL.class);

        MutableJSONEndpoint<SimpleJSONObject> endpoint = new ConcreteMutableJSONEndpoint(mockURL);
        endpoint.setDesiredJSON(json);

        assertTrue(endpoint.canRun());
    }
}
