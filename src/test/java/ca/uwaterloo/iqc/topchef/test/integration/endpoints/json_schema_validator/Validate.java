package ca.uwaterloo.iqc.topchef.test.integration.endpoints.json_schema_validator;

import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Contains integration tests for
 * {@link ca.uwaterloo.iqc.topchef.endpoints.JSONSchemaValidator#validate(Object, Object)}
 */
public final class Validate extends AbstractJSONSchemaValidatorTestCase {
    private Instance instance;
    private Schema schema;

    private static final String instanceString = "{}";
    private static final String schemaString = "{\"type\": \"object\"}";

    @Before
    public void prepareInstances(){
        instance = new Instance();
        schema = new Schema();
    }

    @Test
    public void validationTest() throws Exception {
        assertTrue(this.client.getJSONSchemaValidator().validate(instance, schema));
    }

    @Test
    public void stringValidationTest() throws Exception {
        assertTrue(this.client.getJSONSchemaValidator().validate(instanceString, schemaString));
    }

    @Data
    private final class Instance {
        private final Integer value = 5;
    }

    @Data
    private final class Schema {
        private final String type = "object";
        private final Object properties = new Properties();

        @Data
        private final class Properties {
            private final Object value = new Value();

            @Data
            private final class Value {
                private final String type = "integer";
                private final Integer minimum = 1;
                private final Integer maximum = 10;
            }
        }
    }
}
