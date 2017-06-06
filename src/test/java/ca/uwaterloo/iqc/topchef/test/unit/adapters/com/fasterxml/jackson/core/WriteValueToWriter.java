package ca.uwaterloo.iqc.topchef.test.unit.adapters.com.fasterxml.jackson.core;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for
 * {@link ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper#writeValue(OutputStream, Object)}
 */
@RunWith(JUnitQuickcheck.class)
public final class WriteValueToWriter extends AbstractJacksonTestCase {
    @Property
    public void writeValueToWriter(
            @From(ComplexJSONGenerator.class) OneOfEverythingJSON json
    ) throws Exception {
        Writer writer = new StringWriter();
        ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper();
        mapper.writeValue(writer, json);
        OneOfEverythingJSON newJson = mapper.readValue(writer.toString(), OneOfEverythingJSON.class);
        assertEquals(json, newJson);
    }
}
