package ca.uwaterloo.iqc.topchef.test.unit.adapters.com.fasterxml.jackson.core;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.test.unit.AbstractUnitTestCase;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for
 * {@link ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper#writeValue(Writer, Object)}
 */
@RunWith(JUnitQuickcheck.class)
public final class WriteValueToWriter extends AbstractJacksonTestCase {

    /**
     * Tests that writing JSON to an {@link Writer} results in JSON that can be re-read
     * @param json Generated JSON to write
     * @throws Exception If the underlying test throws an exception
     */
    @Property
    public void writeValueToWriter(
            @From(ComplexJSONGenerator.class) AbstractUnitTestCase.ComplexJSON json
    ) throws Exception {
        Writer writer = new StringWriter();
        ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper();
        mapper.writeValue(writer, json);
        ComplexJSON newJson = mapper.readValue(writer.toString(), ComplexJSON.class);
        assertEquals(json, newJson);
    }
}
