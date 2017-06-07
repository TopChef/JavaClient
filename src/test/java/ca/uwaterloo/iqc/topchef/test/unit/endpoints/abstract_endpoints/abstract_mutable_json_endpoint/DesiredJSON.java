package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_mutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractMutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.MutableJSONEndpoint;
import com.github.dmstocking.optional.java.util.Optional;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Contains unit tests for
 * {@link AbstractMutableJSONEndpoint#getDesiredJSON()}
 *
 * and
 *
 * {@link AbstractMutableJSONEndpoint#setDesiredJSON(Object)}
 */
@RunWith(JUnitQuickcheck.class)
public final class DesiredJSON extends AbstractAbstractMutableJSONEndpointTestCase {
    @Test
    public void defaultJSONIsNull(){
        Mockery context = new Mockery();
        URL mockURL = context.mock(URL.class);

        MutableJSONEndpoint endpoint = new ConcreteMutableJSONEndpoint(mockURL);

        assertFalse(endpoint.getDesiredJSON().isPresent());
    }

    @Property
    public void desiredJSONCanBeSet(
            @From(JSONTypeGenerator.class) SimpleJSONObject json
    ){
        Mockery context = new Mockery();
        URL mockURL = context.mock(URL.class);

        MutableJSONEndpoint<SimpleJSONObject> endpoint = new ConcreteMutableJSONEndpoint(mockURL);
        endpoint.setDesiredJSON(json);

        assertEquals(Optional.of(json), endpoint.getDesiredJSON());
    }
}
