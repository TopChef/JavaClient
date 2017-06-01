package ca.uwaterloo.iqc.topchef.endpoint_models;

import ca.uwaterloo.iqc.topchef.exceptions.UnexpectedResponseCodeException;
import org.json.simple.JSONAware;

import java.io.IOException;

/**
 * Defines a validator capable of seeing whether a JSON object meets a JSON schema.
 */
public interface Validator extends Endpoint {
    /**
     *
     * @param object The instance to validate
     * @param schema The schema against which the object is to be validated
     * @return {@link Boolean#TRUE} if the object validates
     */
    Boolean validate(JSONAware object, JSONAware schema) throws IOException, UnexpectedResponseCodeException;

    /**
     *
     * @param object The instance to validate
     * @param schema The schema against which the object is to be validated
     * @return {@link Boolean#TRUE} if the object validates
     */
    Boolean validate(String object, String schema) throws IOException, UnexpectedResponseCodeException;
}
