package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_immutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.ImmutableJSONEndpoint;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Mockery;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit test for {@link AbstractImmutableJSONEndpoint#getMapper()} and
 * {@link AbstractImmutableJSONEndpoint#setMapper(ObjectMapper)}
 */
@RunWith(JUnitQuickcheck.class)
public final class GetAndSetMapper extends AbstractImmutableJSONEndpointTestCase {
    @Property
    public void mapper(
            @From(ObjectMapperGenerator.class) ObjectMapper mapper
    ){
        Mockery context = new Mockery();

        ImmutableJSONEndpoint endpoint = new ConcreteImmutableJSONEndpoint(context.mock(URL.class));

        endpoint.setMapper(mapper);

        assertEquals(mapper, endpoint.getMapper());
    }
}
