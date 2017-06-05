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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.IsCollectionContaining;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.endpoint_models.JSONSchemaValidator#validate(String, String)}
 */
public final class ValidateStrings extends AbstractJSONSchemaValidatorTestCase {
    private static final String broadestInstance = "{}";
    private static final String broadestSchema = "{\"type\":\"object\"}";

    private static final Mockery context = new Mockery();

    private static final URL mockURL = context.mock(URL.class);

    private static final URLConnection mockConnection = context.mock(URLConnection.class);

    private static final ByteArrayOutputStream mockOutputStream = new ByteArrayOutputStream();

    private static final JSONParser parser = new JSONParser();

    private Validator jsonValidator;

    @Before
    public void getJsonValidator(){
        jsonValidator = new JSONSchemaValidator(mockURL);
    }

    @Test
    public void goodRequest() throws Exception {
        context.checking(new ExpectationsForTrueResult());

        assertTrue(jsonValidator.validate(broadestInstance, broadestSchema));

        JSONObject dataSent = (JSONObject) parser.parse(mockOutputStream.toString());

        assertThat(dataSent.get("object").toString(), IsEqual.equalTo(broadestInstance));
        assertThat(dataSent.get("schema").toString(), IsEqual.equalTo(broadestSchema));

        context.assertIsSatisfied();
    }

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

}
