package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.exceptions.MethodNotAllowedException;
import ca.uwaterloo.iqc.topchef.exceptions.ResourceNotFoundException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Describes an endpoint capable of getting JSON via an HTTP GET request
 */
public interface ImmutableJSONEndpoint extends Endpoint {
    JSONObject getJSON() throws ParseException, IOException, MethodNotAllowedException, ResourceNotFoundException;
}