package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.Endpoint;
import ca.uwaterloo.iqc.topchef.exceptions.UnexpectedResponseCodeException;

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
    Boolean validate(Object object, Object schema) throws IOException, UnexpectedResponseCodeException;

    /**
     *
     * @param object The instance to validate
     * @param schema The schema against which the object is to be validated
     * @return {@link Boolean#TRUE} if the object validates
     */
    Boolean validate(String object, String schema) throws IOException, UnexpectedResponseCodeException;
}
