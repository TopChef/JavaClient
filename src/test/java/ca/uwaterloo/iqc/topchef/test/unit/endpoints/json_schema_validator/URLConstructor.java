package ca.uwaterloo.iqc.topchef.test.unit.endpoints.json_schema_validator;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.endpoints.JSONSchemaValidator;
import ca.uwaterloo.iqc.topchef.endpoints.Validator;
import org.jmock.Mockery;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoints.JSONSchemaValidator#JSONSchemaValidator(URL)}
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
