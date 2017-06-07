package ca.uwaterloo.iqc.topchef.test.unit.adapters.com.fasterxml.jackson.core;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.test.unit.AbstractUnitTestCase;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for
 * {@link ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper#readValue(InputStream, Class)}
 */
@RunWith(JUnitQuickcheck.class)
public final class ReadValueUsingInputStream extends AbstractJacksonTestCase {
    @Property
    public void inputStream(
            @From(ComplexJSONGenerator.class) AbstractUnitTestCase.ComplexJSON json
    ) throws Exception {
        ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper();
        InputStream jsonAsStream = new ByteArrayInputStream(mapper.writeValueAsString(json).getBytes());

        ComplexJSON deserializedJSON = mapper.readValue(jsonAsStream, json.getClass());

        assertEquals(json, deserializedJSON);
    }
}
