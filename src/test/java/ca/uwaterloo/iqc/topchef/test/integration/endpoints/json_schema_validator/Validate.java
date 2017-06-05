package ca.uwaterloo.iqc.topchef.test.integration.endpoints.json_schema_validator;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Contains integration tests for
 * {@link ca.uwaterloo.iqc.topchef.endpoints.JSONSchemaValidator#validate(JSONAware, JSONAware)}
 */
public final class Validate extends AbstractJSONSchemaValidatorTestCase {
    private JSONObject instance;
    private JSONObject schema;

    private static final String instanceString = "{}";
    private static final String schemaString = "{\"type\": \"object\"}";

    @Before
    public void createJSONObjects(){
        instance = new JSONObject();
        instance.put("value", 5);

        getJSONSchema();
    }

    @Test
    public void validationTest() throws Exception {
        assertTrue(this.client.getJSONSchemaValidator().validate(instance, schema));
    }

    private void getJSONSchema(){
        schema = new JSONObject();

        JSONObject value_prop_schema = new JSONObject();
        value_prop_schema.put("type", "integer");
        value_prop_schema.put("minimum", 1);
        value_prop_schema.put("maximum", 10);

        JSONObject property_schema = new JSONObject();
        property_schema.put("value", value_prop_schema);

        schema.put("type", "object");

        schema.put(
                "properties",
                property_schema
        );
    }

    @Test
    public void stringValidationTest() throws Exception {
        assertTrue(this.client.getJSONSchemaValidator().validate(instanceString, schemaString));
    }
}
