package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import ca.uwaterloo.iqc.topchef.exceptions.UnexpectedResponseCodeException;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Endpoint for validating URLs
 */
public class JSONSchemaValidator extends AbstractEndpoint implements Validator {

    /**
     * The log to which runtime messages are written
     */
    private static final Logger log = LoggerFactory.getLogger(JSONSchemaValidator.class);

    private static final JSONParser parser = new JSONParser();

    /**
     *
     * @param url The URL at which the validator is located
     */
    public JSONSchemaValidator(URL url){
        super(url);
    }

    /**
     *
     * @param client The client that this validator is being run from. The validator uses the client's URL resolver to
     *               find where the validator is
     */
    public JSONSchemaValidator(Client client){
        super(client.getURLResolver().getValidatorEndpoint());
    }

    /**
     *
     * @param instance The JSON object to be validated
     * @param schema The schema against which the object is to be validated
     * @return {@link Boolean#TRUE} if the instance matches the schema, otherwise {@link Boolean#FALSE}
     * @throws IOException If I/O cannot be established with the TopChef API
     * @throws UnexpectedResponseCodeException if the response code is not {@link HTTPResponseCode#OK} and
     * {@link HTTPResponseCode#BAD_REQUEST}
     */
    @Override
    public Boolean validate(JSONAware instance, JSONAware schema) throws IOException, UnexpectedResponseCodeException {
        URLConnection connection = openConnection();
        String dataToSend = getDataToSend(instance, schema).toJSONString();
        HTTPResponseCode code = getResponseCode(connection, dataToSend);
        return processResponseCode(code);
    }

    /**
     *
     * @param instance The JSON object to be validated, formatted as a string
     * @param schema The schema against which the object is to be validated
     * @return {@link Boolean#TRUE} if the instance matches the schema, otherwise {@link Boolean#FALSE}
     * @throws IOException If I/O cannot be established with the TopChef API
     * @throws UnexpectedResponseCodeException if the response code is not {@link HTTPResponseCode#OK} and
     * @throws ParseException If the data sent into this method is not valid JSON
     * {@link HTTPResponseCode#BAD_REQUEST}
     */
    @Override
    public Boolean validate(String instance, String schema) throws IOException, UnexpectedResponseCodeException,
            ParseException {
        URLConnection connection = openConnection();
        String dataToSend = getDataToSend(instance, schema).toJSONString();
        HTTPResponseCode code = getResponseCode(connection, dataToSend);
        return processResponseCode(code);
    }

    /**
     *
     * @return An open connection to the TopChef API
     * @throws IOException If I/O cannot be established
     * @throws RuntimeException If the URLConnection cannot be cast to an HTTP request.
     */
    private URLConnection openConnection() throws IOException, RuntimeException {
        URLConnection connection;
        try {
            connection = this.getURL().openConnection();
        } catch (HTTPConnectionCastException error) {
            throw new RuntimeException(error);
        }

        connection.setDoOutput(true);
        connection.setRequestMethod(HTTPRequestMethod.POST);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    /**
     *
     * @param connection The connection to use to get the status code
     * @param dataToSend The request body, formatted as a string
     * @return The HTTP response code of the request
     * @throws IOException If I/O to the server cannot be established
     */
    private HTTPResponseCode getResponseCode(URLConnection connection, String dataToSend) throws IOException {
        OutputStream stream = connection.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(stream);

        log.debug(String.format("Writing data %s to request", dataToSend));

        writer.write(dataToSend);
        writer.close();

        HTTPResponseCode code = connection.getResponseCode();
        connection.disconnect();

        return code;
    }

    /**
     *
     * @param code The response code
     * @return {@link Boolean#TRUE} if the response code is {@link HTTPResponseCode#OK}, and
     * {@link Boolean#FALSE} if the respones code is {@link HTTPResponseCode#BAD_REQUEST}
     * @throws UnexpectedResponseCodeException If a different status code is received
     */
    private Boolean processResponseCode(HTTPResponseCode code) throws UnexpectedResponseCodeException {
        switch (code) {
            case OK:
                return Boolean.TRUE;
            case BAD_REQUEST:
                return Boolean.FALSE;
            default:
                throw new UnexpectedResponseCodeException(String.format(
                        "Request returned unexpected response %s",
                        code
                ));
        }
    }

    /**
     *
     * @param instance The instance to send
     * @param schema The schema to match
     * @return The request formatted as JSON
     */
    @SuppressWarnings("unchecked")
    private static JSONObject getDataToSend(JSONAware instance, JSONAware schema){
        JSONObject data = new JSONObject();
        data.put("object", instance);
        data.put("schema", schema);

        return data;
    }

    /**
     *
     * @param instance The instance to send
     * @param schema The schema to match
     * @return The request formatted as JSON
     */
    @SuppressWarnings("unchecked")
    private static JSONObject getDataToSend(String instance, String schema) throws ParseException {
        JSONObject instanceJSON;
        JSONObject schemaJSON;

        try {
            instanceJSON = (JSONObject) parser.parse(instance);
            schemaJSON = (JSONObject) parser.parse(schema);
        } catch (ClassCastException error){
            log.error("Attempting to cast the parsed JSON into JSON objects threw error", error);
            throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION, error);
        }

        JSONObject data = new JSONObject();
        data.put("object", instanceJSON);
        data.put("schema", schemaJSON);

        return data;
    }
}
