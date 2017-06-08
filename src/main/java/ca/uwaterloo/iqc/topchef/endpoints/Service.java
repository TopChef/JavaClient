package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.ImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;

import java.io.IOException;
import java.util.UUID;

/**
 * Maps the /services/\<service_id\> endpoint
 */
public interface Service extends ImmutableJSONEndpoint {
    /**
     *
     * @return The ID of the service
     */
    UUID getServiceID();

    /**
     *
     * @return The schema that must be valid before a job can be posted to the API
     * @throws IOException If the server cannot be contacted
     * @throws HTTPException If the server does something weird
     */
    Object getJobRegistrationSchema() throws IOException, HTTPException;

    /**
     * Let the server know that this service is still alive
     */
    void checkIn() throws HTTPException, IOException;
}
