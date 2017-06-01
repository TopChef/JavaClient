package ca.uwaterloo.iqc.topchef.endpoint_models;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Endpoint for validating URLs
 */
public class JSONSchemaValidator extends AbstractEndpoint implements Validator {
    public JSONSchemaValidator(URL url){
        super(url);
    }

    public JSONSchemaValidator(Client client){
        super(client.getURLResolver().getValidatorEndpoint());
    }

    @Override
    public Boolean validate(JSONAware instance, JSONAware schema) throws IOException, RuntimeException {
        URLConnection connection = openConnection();

        JSONObject dataToSend = getDataToSend(instance, schema);

        OutputStream stream = connection.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(stream);

        writer.write(dataToSend.toJSONString());
        writer.close();

        HTTPResponseCode code = connection.getResponseCode();

        switch (code) {
            case OK:
                return Boolean.TRUE;
            case BAD_REQUEST:
                return Boolean.FALSE;
            default:
                throw new IOException(String.format(
                        "Request returned unexpected response %s",
                        code
                ));
        }
    }

    private static JSONObject getDataToSend(JSONAware instance, JSONAware schema){
        JSONObject data = new JSONObject();
        data.put("object", instance.toJSONString());
        data.put("schema", schema.toJSONString());

        return data;
    }

    private static JSONObject getDataToSend(String instance, String schema){
        JSONObject data = new JSONObject();
        data.put("object", instance);
        data.put("schema", schema);

        return data;
    }

    private URLConnection openConnection() throws IOException, RuntimeException {
        URLConnection connection;
        try {
            connection = this.getURL().openConnection();
        } catch (HTTPConnectionCastException error) {
            throw new RuntimeException(error);
        }
        connection.setRequestMethod(HTTPRequestMethod.POST);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }
}
