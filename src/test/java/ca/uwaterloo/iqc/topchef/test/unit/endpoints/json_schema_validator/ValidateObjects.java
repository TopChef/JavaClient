package ca.uwaterloo.iqc.topchef.test.unit.endpoints.json_schema_validator;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.JSONSchemaValidator;
import ca.uwaterloo.iqc.topchef.endpoints.Validator;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import ca.uwaterloo.iqc.topchef.exceptions.UnexpectedResponseCodeException;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jetbrains.annotations.Contract;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoints.JSONSchemaValidator#validate(Object, Object)}
 */
@RunWith(JUnitQuickcheck.class)
public final class ValidateObjects extends AbstractJSONSchemaValidatorTestCase {
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

    @Test
    public void testOpenConnectionClassCastError() throws Exception {
        context.checking(new ExpectationsForConnectionCastError());
        expectedException.expect(RuntimeException.class);

        validator.validate(new Object(), new Object());

        context.assertIsSatisfied();
    }

    @Property
    public void goodResponseCode(
            @From(ComplexJSONGenerator.class) ComplexJSON json
    ) throws Exception {
        context.checking(new ExpectationsForGoodResponseCode());

        assertTrue(validator.validate(json, json));

        context.assertIsSatisfied();
    }

    @Property
    public void badResponseCode(
            @From(ComplexJSONGenerator.class) ComplexJSON json
    ) throws Exception {
        context.checking(new ExpectationsForFalseResponseCode());

        assertFalse(validator.validate(json, json));

        context.assertIsSatisfied();
    }

    @Property
    public void errorResponseCode(
            @From(ComplexJSONGenerator.class) ComplexJSON json
    ) throws Exception {
        context.checking(new ExpectationsForErrorResponseCode());
        expectedException.expect(UnexpectedResponseCodeException.class);

        validator.validate(json, json);

        context.assertIsSatisfied();
    }

    private final class ExpectationsForConnectionCastError extends Expectations {
        public ExpectationsForConnectionCastError() throws Exception {
            oneOf(mockURL).openConnection();
            will(throwException(new HTTPConnectionCastException("Kaboom")));
        }
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

    private final class ExpectationsForFalseResponseCode extends ExpectationsWithResponseCode {
        public ExpectationsForFalseResponseCode() throws Exception {
            super();
        }

        @Contract(pure = true)
        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.BAD_REQUEST;
        }
    }

    private final class ExpectationsForErrorResponseCode extends ExpectationsWithResponseCode {
        public ExpectationsForErrorResponseCode() throws Exception {
            super();
        }

        @Contract(pure = true)
        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.INTERNAL_SERVER_ERROR;
        }
    }
}
