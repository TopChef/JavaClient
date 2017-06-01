package ca.uwaterloo.iqc.topchef.endpoint_models;

import org.json.simple.JSONAware;

import java.io.IOException;

/**
 * Created by mkononen on 01/06/17.
 */
public interface Validator extends Endpoint {
    /**
     *
     * @param object The instance to validate
     * @param schema The schema against which the object is to be validated
     * @return {@link Boolean#TRUE} if the object validates
     */
    Boolean validate(JSONAware object, JSONAware schema) throws IOException;
}
