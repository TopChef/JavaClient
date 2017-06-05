package ca.uwaterloo.iqc.topchef.test.unit.endpoint_models.json_schema_validator;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoint_models.JSONSchemaValidator;
import ca.uwaterloo.iqc.topchef.endpoint_models.Validator;
import org.hamcrest.core.IsEqual;
import org.jetbrains.annotations.Contract;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoint_models.JSONSchemaValidator#validate(String, String)}
 */
public final class ValidateStrings extends AbstractJSONSchemaValidatorTestCase {

    /**
     * The instance to be validated
     */
    private static final String broadestInstance = "{}";

    /**
     * A schema that the instance matches
     */
    private static final String broadestSchema = "{\"type\":\"object\"}";

    /**
     * An array to be used to check that a {@link ParseException} is thrown on parsing
     */
    private static final String arrayJSON = "[{\"type\": \"object\"}]";

    /**
     * The mocking context
     */
    private final Mockery context = new SynchronizedContext();

    /**
     * The URL to the validator endpoint
     */
    private final URL mockURL = context.mock(URL.class);

    /**
     * The connection to the validator
     */
    private final URLConnection mockConnection = context.mock(URLConnection.class);

    /**
     * The output stream that the mock connection returns. The body of the POST request is written to
     * this stream
     */
    private final ByteArrayOutputStream mockOutputStream = new ByteArrayOutputStream();

    /**
     * A JSON parser used to parse the mock output stream and check that the correct data is sent in
     * the request body
     */
    private final JSONParser parser = new JSONParser();

    /**
     * The validator endpoint
     */
    private Validator jsonValidator;

    /**
     * Checks that exceptions of a particular type are thrown
     */
    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    /**
     * Set the JSON validator to use the mock URL endpoint
     */
    @Before
    public void getJsonValidator(){
        jsonValidator = new JSONSchemaValidator(mockURL);
    }

    @After
    public void clearOutputStream(){
        mockOutputStream.reset();
    }

    /**
     * Tests that a request made along the happy path sends the correct data to the API
     *
     * @throws Exception If the underlying test throws an exception. This shouldn't happen
     */
    @Test
    public void goodRequest() throws Exception {
        context.checking(new ExpectationsForTrueResult());
        assertTrue(jsonValidator.validate(broadestInstance, broadestSchema));
        JSONObject dataSent = (JSONObject) parser.parse(mockOutputStream.toString());

        assertThat(dataSent.get("object").toString(), IsEqual.equalTo(broadestInstance));
        assertThat(dataSent.get("schema").toString(), IsEqual.equalTo(broadestSchema));

        context.assertIsSatisfied();
    }

    /**
     * Tests that a {@link ParseException} is thrown if the JSON schema starts as an array
     *
     * @throws Exception If the underlying test throws one
     */
    @Test
    public void requestIsArray() throws Exception {
        context.checking(new ExpectationsForFalseResult());

        exceptionGrabber.expect(ParseException.class);

        jsonValidator.validate(broadestInstance, arrayJSON);

        context.assertIsSatisfied();
    }

    /**
     * Describes mock expectations for the test
     */
    private abstract class ExpectationsForRequest extends Expectations {
        public ExpectationsForRequest() throws Exception {
            oneOf(mockURL).openConnection();
            will(returnValue(mockConnection));

            oneOf(mockConnection).getOutputStream();
            will(returnValue(mockOutputStream));

            oneOf(mockConnection).setRequestMethod(HTTPRequestMethod.POST);
            oneOf(mockConnection).setRequestProperty("Content-Type", "application/json");

            oneOf(mockConnection).getResponseCode();
            will(returnValue(getResponseCode()));

            oneOf(mockConnection).setDoOutput(Boolean.TRUE);
            oneOf(mockConnection).disconnect();
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

    private static final class SynchronizedContext extends Mockery {
        public SynchronizedContext(){
            setThreadingPolicy(new Synchroniser());
        }
    }

}
