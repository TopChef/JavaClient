package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.ImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import com.github.dmstocking.optional.java.util.Optional;

import java.io.IOException;
import java.util.List;
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
     * @return The name of the service
     * @throws IOException If the server cannot be contacted
     * @throws HTTPException If the server does something weird
     */
    String getName() throws HTTPException, IOException;

    /**
     *
     * @return The schema that must be valid before a job can be posted to the API
     * @throws IOException If the server cannot be contacted
     * @throws HTTPException If the server does something weird
     */
    Object getJobRegistrationSchema() throws IOException, HTTPException;

    /**
     * Let the server know that this service is still alive
     * @throws IOException If the server cannot be contacted
     * @throws HTTPException If the server does something weird
     */
    void checkIn() throws HTTPException, IOException;

    /**
     *
     * @return A list of jobs that have been registered with this service
     * @throws HTTPException If the server does something weird
     * @throws IOException If the server cannot be contacted
     */
    List<Job> getJobs() throws HTTPException, IOException;

    /**
     *
     * @return The next job in the queue, if there is one
     * @throws IOException If the server cannot be contacted
     * @throws HTTPException If the server does something weird
     */
    Optional<Job> getNextJob() throws HTTPException, IOException;

    Boolean hasTimedOut() throws HTTPException, IOException;

    <T extends Service> Boolean equals(T other);
}
