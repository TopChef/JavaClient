package ca.uwaterloo.iqc.topchef.test.unit.adapters.com.fasterxml.jackson.core;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.test.unit.AbstractUnitTestCase;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for
 * {@link ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper#writeValue(OutputStream, Object)}
 */
@RunWith(JUnitQuickcheck.class)
public final class WriteValueToOutputStream extends AbstractJacksonTestCase {
    @Property
    public void writeValueToStream(
            @From(ComplexJSONGenerator.class) AbstractUnitTestCase.ComplexJSON json
    ) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper();
        mapper.writeValue(stream, json);

        ComplexJSON deserializedJSON = mapper.readValue(stream.toString(), ComplexJSON.class);
        assertEquals(json, deserializedJSON);
    }
}
