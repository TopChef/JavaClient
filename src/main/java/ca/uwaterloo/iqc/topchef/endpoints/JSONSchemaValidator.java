package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import ca.uwaterloo.iqc.topchef.exceptions.UnexpectedResponseCodeException;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

/**
 * Endpoint for validating URLs
 */
public class JSONSchemaValidator extends AbstractEndpoint implements Validator {

    private ObjectMapper jsonMapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper
            .ObjectMapper();

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
    public Boolean validate(Object instance, Object schema) throws IOException, UnexpectedResponseCodeException {
        URLConnection connection = openConnection();

        ValidationRequest request = new ValidationRequest();
        request.setSchema(schema);
        request.setObject(instance);

        return validateRequest(request, connection);
    }

    /**
     *
     * @param instance The JSON object to be validated, formatted as a string
     * @param schema The schema against which the object is to be validated
     * @return {@link Boolean#TRUE} if the instance matches the schema, otherwise {@link Boolean#FALSE}
     * @throws IOException If I/O cannot be established with the TopChef API
     * @throws UnexpectedResponseCodeException if the response code is not {@link HTTPResponseCode#OK} or
     * {@link HTTPResponseCode#BAD_REQUEST}
     */
    @Override
    public Boolean validate(String instance, String schema) throws IOException, UnexpectedResponseCodeException {
        URLConnection connection = openConnection();

        ValidationRequest request = new ValidationRequest();
        request.setSchema(jsonMapper.readValue(schema));
        request.setObject(jsonMapper.readValue(instance));

        return validateRequest(request, connection);
    }

    /**
     * Open the connection, and write down the JSON
     *
     * @param request The request to be sent to the server
     * @param connection The connection to use
     * @return True if the JSON is valid, otherwise False
     * @throws IOException If I/O is bad
     * @throws UnexpectedResponseCodeException If the response code is something other than
     * {@link HTTPResponseCode#OK} or {@link HTTPResponseCode#BAD_REQUEST}
     */
    private Boolean validateRequest(ValidationRequest request, URLConnection connection) throws IOException,
            UnexpectedResponseCodeException {
        try {
            connection.connect();
            jsonMapper.writeValue(connection.getOutputStream(), request);
            return processResponseCode(connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
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

        connection.setDoOutput(Boolean.TRUE);
        connection.setRequestMethod(HTTPRequestMethod.POST);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    /**
     *
     * @param code The response code
     * @return {@link Boolean#TRUE} if the response code is {@link HTTPResponseCode#OK}, and
     * {@link Boolean#FALSE} if the response code is {@link HTTPResponseCode#BAD_REQUEST}
     * @throws UnexpectedResponseCodeException If a different status code is received
     */
    private static Boolean processResponseCode(HTTPResponseCode code) throws UnexpectedResponseCodeException {
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
     * A representation of the JSON to be sent to the validator via a POST request
     */
    private static final class ValidationRequest {
        /**
         * The instance to check
         */
        @Getter
        @Setter
        private Object object;

        /**
         * The desired schema
         */
        @Getter
        @Setter
        private Object schema;
    }
}
