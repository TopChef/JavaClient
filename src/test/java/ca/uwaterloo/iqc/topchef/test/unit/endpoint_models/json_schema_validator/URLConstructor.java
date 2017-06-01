package ca.uwaterloo.iqc.topchef.test.unit.endpoint_models.json_schema_validator;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoint_models.JSONSchemaValidator;
import ca.uwaterloo.iqc.topchef.endpoint_models.Validator;
import org.jmock.Mockery;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoint_models.JSONSchemaValidator#JSONSchemaValidator(URL)}
 */
public final class URLConstructor extends AbstractJSONSchemaValidatorTestCase {
    private static final Mockery context = new Mockery();

    private static final URL url = context.mock(URL.class);

    @Test
    public void testConstructor(){
        Validator validator = new JSONSchemaValidator(url);
        assertEquals(url, validator.getURL());
    }
}
