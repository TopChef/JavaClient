package ca.uwaterloo.iqc.topchef.test.unit.endpoints.json_schema_validator;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.JSONSchemaValidator;
import ca.uwaterloo.iqc.topchef.endpoints.Validator;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jetbrains.annotations.Contract;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertTrue;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoints.JSONSchemaValidator#validate(Object, Object)}
 */
@RunWith(JUnitQuickcheck.class)
public final class ValidateStrings extends AbstractJSONSchemaValidatorTestCase {
    private final Mockery context = new Mockery();

    private final URL mockURL = context.mock(URL.class);

    private final URLConnection mockConnection = context.mock(URLConnection.class);

    private OutputStream mockOutputstream;

    private Validator validator;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void constructValidator(){
        validator = new JSONSchemaValidator(mockURL);
    }

    @Before
    public void constructOutputStream(){
        mockOutputstream = new ByteArrayOutputStream();
    }

    @Property
    public void stringsValidate(
            @From(ComplexJSONGenerator.class) ComplexJSON json
    ) throws Exception {
        context.checking(new ExpectationsForGoodResponseCode());

        String jsonAsString = new ObjectMapper().writeValueAsString(json);

        assertTrue(validator.validate(jsonAsString, jsonAsString));

        context.assertIsSatisfied();
    }

    private abstract class ExpectationsWithResponseCode extends Expectations {
        public ExpectationsWithResponseCode() throws Exception {
            oneOf(mockURL).openConnection();
            will(returnValue(mockConnection));

            expectationsForConnectionConfiguring();
            oneOf(mockConnection).connect();
            oneOf(mockConnection).disconnect();

            oneOf(mockConnection).getOutputStream();
            will(returnValue(mockOutputstream));

            oneOf(mockConnection).getResponseCode();
            will(returnValue(getResponseCode()));
        }

        private void expectationsForConnectionConfiguring() throws Exception {
            oneOf(mockConnection).setRequestMethod(HTTPRequestMethod.POST);
            oneOf(mockConnection).setRequestProperty("Content-Type", "application/json");
            oneOf(mockConnection).setDoOutput(Boolean.TRUE);
        }

        protected abstract HTTPResponseCode getResponseCode();
    }

    private final class ExpectationsForGoodResponseCode extends ExpectationsWithResponseCode {
        public ExpectationsForGoodResponseCode() throws Exception {
            super();
        }

        @Contract(pure = true)
        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.OK;
        }
    }
}
