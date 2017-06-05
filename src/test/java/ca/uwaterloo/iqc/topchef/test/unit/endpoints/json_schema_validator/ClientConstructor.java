package ca.uwaterloo.iqc.topchef.test.unit.endpoints.json_schema_validator;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.endpoints.JSONSchemaValidator;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * Contains unit tests for
 * {@link ca.uwaterloo.iqc.topchef.endpoints.JSONSchemaValidator#JSONSchemaValidator(Client)}
 */
public final class ClientConstructor extends AbstractJSONSchemaValidatorTestCase {
    private static final Mockery context = new Mockery();

    private static final Client mockClient = context.mock(Client.class);

    private static final URLResolver mockResolver = context.mock(URLResolver.class);

    @Test
    public void clientConstructor(){
        context.checking(new ExpectationsForTest());
        new JSONSchemaValidator(mockClient);
        context.assertIsSatisfied();
    }

    private final class ExpectationsForTest extends Expectations {
        public ExpectationsForTest(){
            oneOf(mockClient).getURLResolver();
            will(returnValue(mockResolver));

            oneOf(mockResolver).getValidatorEndpoint();
        }
    }
}
