package ca.uwaterloo.iqc.topchef.test.unit.adapters.com.fasterxml.jackson.core;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

/**
 * Contains unit tests for
 * {@link ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper#readValue(String)}
 */
@RunWith(JUnitQuickcheck.class)
public final class ReadToObject extends AbstractJacksonTestCase {
    @Property
    public void writesToObject(
            @From(ComplexJSONGenerator.class) OneOfEverythingJSON json
    ) throws Exception {
        ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper();
        String jsonAsString = mapper.writeValueAsString(json);
        Object result = mapper.readValue(jsonAsString);
        assertNotNull(result);
    }
}
