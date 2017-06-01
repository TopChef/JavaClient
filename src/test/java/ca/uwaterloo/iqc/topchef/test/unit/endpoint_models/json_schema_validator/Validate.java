package ca.uwaterloo.iqc.topchef.test.unit.endpoint_models.json_schema_validator;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoint_models.JSONSchemaValidator;
import ca.uwaterloo.iqc.topchef.endpoint_models.Validator;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import org.jetbrains.annotations.Contract;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.json.simple.JSONAware;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoint_models.JSONSchemaValidator#validate(JSONAware, JSONAware)}
 */
public final class Validate extends AbstractJSONSchemaValidatorTestCase {
    private static final Mockery context = new Mockery();

    private static final JSONAware instance = context.mock(JSONAware.class, "instance");

    private static final JSONAware schema = context.mock(JSONAware.class, "schema");

    private static final URLConnection connection = context.mock(URLConnection.class);

    private static final URL url = context.mock(URL.class);

    private Validator jsonValidator;

    private OutputStream mockOutputStream = new ByteArrayOutputStream();

    @Before
    public void getValidator(){
        jsonValidator = new JSONSchemaValidator(url);
    }

    @Test(expected = RuntimeException.class)
    public void classCastExceptionThrownWithURL() throws Exception {
        context.checking(new ExpectationsForConnectionErrorTest());

        jsonValidator.validate(instance, schema);

        context.assertIsSatisfied();
    }

    @Test
    public void badRequestTest() throws Exception {
        context.checking(new ExpectationsForFalseResult());

        assertFalse(jsonValidator.validate(instance, schema));

        context.assertIsSatisfied();
    }

    @Test
    public void goodRequestTest() throws Exception {
        context.checking(new ExpectationsForTrueResult());

        assertTrue(jsonValidator.validate(instance, schema));

        context.assertIsSatisfied();
    }

    @Test(expected = IOException.class)
    public void errorRequestTest() throws Exception {
        context.checking(new ExpectationsForErrorResult());

        jsonValidator.validate(instance, schema);

        context.assertIsSatisfied();
    }

    private final class ExpectationsForConnectionErrorTest extends Expectations {
        public ExpectationsForConnectionErrorTest() throws Exception {
            oneOf(url).openConnection();
            will(throwException(new HTTPConnectionCastException("kaboom")));
        }
    }

    private abstract class ExpectationsForRequest extends Expectations {
        public ExpectationsForRequest() throws Exception {
            oneOf(url).openConnection();
            will(returnValue(connection));

            oneOf(connection).getOutputStream();
            will(returnValue(mockOutputStream));

            oneOf(connection).setRequestMethod(HTTPRequestMethod.POST);
            oneOf(connection).setRequestProperty("Content-Type", "application/json");

            oneOf(connection).getResponseCode();
            will(returnValue(getResponseCode()));

            oneOf(schema).toJSONString();
            oneOf(instance).toJSONString();
        }

        protected abstract HTTPResponseCode getResponseCode();
    }

    private final class ExpectationsForFalseResult extends ExpectationsForRequest {

        public ExpectationsForFalseResult() throws Exception {
            super();
        }

        @Contract(pure = true)
        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.BAD_REQUEST;
        }
    }

    private final class ExpectationsForTrueResult extends ExpectationsForRequest {
        public ExpectationsForTrueResult() throws Exception {
            super();
        }

        @Contract(pure = true)
        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.OK;
        }
    }

    private final class ExpectationsForErrorResult extends ExpectationsForRequest {
        public ExpectationsForErrorResult() throws Exception {
            super();
        }

        @Contract(pure = true)
        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.INTERNAL_SERVER_ERROR;
        }
    }
}
