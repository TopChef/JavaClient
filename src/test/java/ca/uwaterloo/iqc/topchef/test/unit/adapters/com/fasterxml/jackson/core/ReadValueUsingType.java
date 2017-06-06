package ca.uwaterloo.iqc.topchef.test.unit.adapters.com.fasterxml.jackson.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for
 * {@link ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper#readValue(String, Class)}
 */
public final class ReadValueUsingType extends AbstractJacksonTestCase {
    @Test
    public void readValue() throws Exception {
        String json = mapper.writeValueAsString(nestedJSON);
        NestedJSON object = mapper.readValue(json, NestedJSON.class);

        assertEquals(nestedJSON, object);
    }
}
