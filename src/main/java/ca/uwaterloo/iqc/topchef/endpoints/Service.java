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

    /**
     *
     * @return {@link Boolean#TRUE} if the service has timed out. Otherwise, it returns {@link Boolean#FALSE}
     * @throws HTTPException If the server returns an unexpected HTTP response
     * @throws IOException If the server cannot be contacted
     */
    Boolean hasTimedOut() throws HTTPException, IOException;

    /**
     *
     * @param other The instance against which this service is to be compared for equality
     * @param <T> The type against which this is to be compared
     * @return {@link Boolean#TRUE} if the services are equal, and {@link Boolean#FALSE} if not
     */
    <T extends Service> Boolean equals(T other);
}
