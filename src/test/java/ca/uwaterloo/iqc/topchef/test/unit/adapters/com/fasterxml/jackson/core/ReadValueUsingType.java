package ca.uwaterloo.iqc.topchef.test.unit.adapters.com.fasterxml.jackson.core;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.test.unit.AbstractUnitTestCase;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for
 * {@link ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper#readValue(String, Class)}
 */
@RunWith(JUnitQuickcheck.class)
public final class ReadValueUsingType extends AbstractJacksonTestCase {
    @Property
    public void serializeAndDeserialize(
            @From(ComplexJSONGenerator.class) AbstractUnitTestCase.ComplexJSON json
    ) throws Exception {
        ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper();
        String serialized_object = mapper.writeValueAsString(json);

        assertEquals(json, mapper.readValue(serialized_object, json.getClass()));
    }
}
