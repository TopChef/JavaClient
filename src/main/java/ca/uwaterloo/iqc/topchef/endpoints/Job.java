package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.Endpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;

import java.io.IOException;
import java.util.UUID;

/**
 * Describes a job
 */
public interface Job extends Endpoint {

    /**
     *
     * @return The ID of this job
     */
    UUID getID();

    /**
     *
     * @param <T> The type to which the job parameters are to be marshalled
     * @return An instance of the provided type that contains the job parameters
     * @throws HTTPException If the server does something weird
     * @throws IOException If the server cannot be contacted
     */
    <T> T getParameters() throws HTTPException, IOException;

    /**
     *
     * @param parameters The new job parameters
     * @throws HTTPException If the server does something weird
     * @throws IOException If the server cannot be contacted
     */
    <T> void setParameters(T parameters) throws HTTPException, IOException;

    /**
     *
     * @return The current status of this job
     * @throws HTTPException If the server does something weird
     * @throws IOException If the server cannot be contacted
     */
    Status getStatus() throws HTTPException, IOException;

    /**
     *
     * @param status The desired status to which this job is to be set
     * @throws HTTPException If the server does something weird
     * @throws IOException If the server cannot be contacted
     */
    void setStatus(Status status) throws HTTPException, IOException;

    <T> T getResult() throws HTTPException, IOException;

    <T> void setResult(T result) throws HTTPException, IOException;

    /**
     * If the incoming result is a string, parse that string as JSON before
     * setting the job result.
     *
     * @param result The job result
     * @throws HTTPException If the server does something weird
     * @throws IOException If the server cannot be contacted
     */
    void setResult(String result) throws HTTPException, IOException;

    /**
     * The execution status of a particular job
     */
    enum Status {
        REGISTERED,
        WORKING,
        COMPLETED
    }
}
